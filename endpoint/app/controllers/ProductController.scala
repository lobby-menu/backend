package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents, PlayBodyParsers}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.concurrent.ExecutionContext.Implicits.global
import org.tubit.lobby.products.JsonFormats._
import org.tubit.lobby.products.Product
import play.api.libs.json.Json
import reactivemongo.play.json.collection.JSONCollection

class ProductController @Inject() (cc: ControllerComponents,
val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
  override lazy val parse: PlayBodyParsers = cc.parsers
  private val categoryCollection = db[JSONCollection]("products")

  private def mapProduct(product: Product) : Product = {
    product
  }

  def list(category: Option[String]) = Action.async{
    val query = category
      .map{ category => Json.obj("categories" -> category) }
      .getOrElse(Json.obj())
    val cursor = categoryCollection.find(query).cursor[Product]()

    cursor.collect[List]().map{ list =>
      Ok(Json.stringify(
        Json.toJson(list.map(mapProduct))
      ))
    }
  }
}
