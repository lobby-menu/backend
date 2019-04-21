package org.tubit.lobby
import org.tubit.lobby.products.SingleBuy
import play.api.libs.json.{JsObject, JsValue, Json, OWrites}
import play.api.libs.ws.WSClient

import scala.concurrent.Future

case class PrintRequestObject (
  orderId: String,
  creation_date: Long,
  table: Int,
  message: Option[String],
  orders: Seq[SingleBuy],
)

case class PrintRequest(ws: WSClient, baseUrl: String) {
  import scala.concurrent.ExecutionContext.Implicits._

  implicit object SingleBuyWrites extends OWrites[SingleBuy]{
    override def writes(single: SingleBuy): JsObject = {
      val item = single.item

      JsObject(Seq(
        ("count" -> Json.toJson(single.count)),
        ("item" -> Json.obj(
          "id" -> item.id.get,
          "name" -> item.name,
          "price" -> item.price
        ))
      ))
    }
  }

  implicit object PrintRequestWrites extends OWrites[PrintRequestObject]{
    override def writes(pro: PrintRequestObject): JsObject = Json.obj(
      "orderId" -> pro.orderId,
      "table" -> pro.table,
      "creation_date" -> pro.creation_date,
      "orders" -> Json.toJson(pro.orders),
    )
  }

  def send(pro: PrintRequestObject) : Future[JsValue] = {
    ws
      .url(baseUrl + "/print")
      .addHttpHeaders(("Content-Type", "application/json"))
      .post(Json.toJson(pro))
      .map(resp => resp.json)
  }

  def format(pro: PrintRequestObject) : JsValue = Json.toJson(pro)
}
