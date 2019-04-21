package controllers

import javax.inject.Inject
import org.tubit.lobby.{PrintRequest, PrintRequestObject}
import org.tubit.lobby.products.{Order, Product, SingleBuy}
import play.api.mvc.{AbstractController, ControllerComponents, PlayBodyParsers}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import org.tubit.lobby.products.JsonFormats._
import play.api.Configuration
import play.api.libs.json.{JsError, JsObject, JsSuccess, Json}
import play.api.libs.ws.WSClient
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.BSONObjectIDFormat

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrderController @Inject()(
  config: Configuration,
  ws: WSClient,
  cc: ControllerComponents,
  val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
  override lazy val parse: PlayBodyParsers = cc.parsers
  private lazy val printRequest = PrintRequest(ws, config.get[String]("print.baseUrl"))
  private val productCollection = db[JSONCollection]("products")
  private val orderCollection = db[JSONCollection]("orders")

  def getOrderCursor(
    done: Option[Boolean],
    table: Option[Int],
    bio: Option[String],
    ids: Option[Seq[String]]
  ) : Cursor[Order] = {
    val query = (
      done.map(d => Json.obj("done" -> d)).getOrElse(Json.obj())
      ++ table.map(table => Json.obj("table" -> table)).getOrElse(Json.obj())
      ++ bio.map(bio => Json.obj("bio_identity" -> bio)).getOrElse(Json.obj())
      ++
        ids
          .map(ids => ids.flatMap(BSONObjectID.parse(_).toOption))
          .map(ids => JsObject(Seq("_id" -> Json.obj("$in" -> ids))))
          .getOrElse(Json.obj())
    )

    orderCollection.find(query).cursor[Order]()
  }

  def list(done: Option[Boolean], table: Option[Int], bio: Option[String]) = Action.async{
    val cursor = getOrderCursor(done, table, bio, None)

    cursor.collect[List]().map{ list =>
      Ok(Json.stringify(
        Json.toJson(list)
      ))
    }
  }

  def getAllProducts() : Future[Seq[Product]] = {
    val query = Json.obj()
    val cursor = productCollection.find(query).cursor[Product]()

    cursor.collect[List]()
  }

  def createPrintRequestObject(order: Order, products: Seq[Product]) : PrintRequestObject = {
    val realOrders = order.orders.map(sb => SingleBuy(
      sb.count,
      products.find(p => p.id.exists(id => id == sb.item.id.get)).get
    ))

    PrintRequestObject(
      order.id.get,
      order.creation_date,
      order.table,
      order.message,
      realOrders
    )
  }

  def getSingleOrderPrintRequestObject(orderId: String): Future[PrintRequestObject] = {
    getAllProducts()
      .flatMap(products =>
        getOrderCursor(None, None, None, Some(Seq(orderId)))
          .collect[List]()
          .map(orders => orders.head)
          .map(order => createPrintRequestObject(order, products))
      )
  }

  def print(orderId: String) = Action.async {
    getSingleOrderPrintRequestObject(orderId)
      .flatMap(printRequest.send)
      .map(result => Ok(Json.stringify(result)))
  }

  def detail(orderId: String) = Action.async {
    getSingleOrderPrintRequestObject(orderId)
      .map(printRequest.format)
      .map(result => Ok(Json.stringify(result)))
  }

  def done = Action.async{ implicit request =>
    val result = request.body.asJson.map(result => Json.fromJson[List[String]](result))

    if(result.isEmpty) Future{ Ok(Json.stringify(Json.obj("ok" -> false, "reason" -> "Not a valid list of orders."))) }
    else
      result.get match {
        case JsSuccess(list, _) => {
          orderCollection
            .update(
              Json.obj("_id" -> Json.obj("$in" -> Json.toJson(list.map(id => Json.obj("$oid" -> id))))),
              Json.obj("$set" -> Json.obj("done" -> true)),
              db.connection.options.writeConcern,
              false,
              true
            ).map(result => {
            Ok(Json.stringify(Json.obj("ok" -> true)))
          })
        }
        case _ => Future { Ok(Json.stringify(Json.obj("ok" -> false, "reason" -> "Not a valid list of orders."))) }
      }

  }

  def submit = Action.async{ implicit request =>
    val result = request.body.asJson.map(OrderReads.reads)

    if(result.isEmpty) Future{ Ok(Json.stringify(Json.obj("ok" -> false, "reason" -> "Couldn't read the order."))) }
    else {
      (result.get match{
        case JsSuccess(order, _) => orderCollection.insert(order).map(_ => Json.obj("ok" -> true))
        case JsError(e) => Future{ Json.obj("ok" -> false, "reason" -> "Couldn't parse the order." )}
      }).map(obj => Ok(Json.stringify(obj)))
    }
  }

}
