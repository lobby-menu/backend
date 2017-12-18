package controllers

import javax.inject.Inject

import org.tubit.lobby.products.Category
import org.tubit.lobby.products.JsonFormats._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

class Application @Inject() (
  cc: ControllerComponents,
  val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
  override lazy val parse: PlayBodyParsers = cc.parsers
  private val categoryCollection = db[JSONCollection]("categories")

  def index = Action.async {
    val cursor = categoryCollection.find(Json.obj()).cursor[Category]()

    cursor.collect[List]().map{ list =>
      Ok(Json.stringify(Json.toJson(list)))
    }
  }
}
