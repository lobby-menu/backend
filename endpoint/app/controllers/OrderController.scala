package controllers

import javax.inject.Inject

import org.tubit.lobby.products.{Category, Order}
import play.api.mvc.{AbstractController, ControllerComponents, PlayBodyParsers}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import org.tubit.lobby.products.JsonFormats._
import play.api.libs.json.{JsError, JsSuccess, Json}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrderController @Inject()(
  cc: ControllerComponents,
  val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
  override lazy val parse: PlayBodyParsers = cc.parsers
  private val orderCollection = db[JSONCollection]("orders")

  def list = Action.async{
    val cursor = orderCollection.find(Json.obj()).cursor[Order]()

    cursor.collect[List]().map{ list =>
      Ok(Json.stringify(
        Json.toJson(list)
      ))
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
