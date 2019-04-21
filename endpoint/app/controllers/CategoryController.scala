package controllers

import javax.inject.Inject

import org.tubit.lobby.products.Category
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, PlayBodyParsers}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection
import org.tubit.lobby.products.JsonFormats._
import scala.concurrent.ExecutionContext.Implicits.global

class CategoryController @Inject() (
  cc: ControllerComponents,
  val reactiveMongoApi: ReactiveMongoApi
) extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
  override lazy val parse: PlayBodyParsers = cc.parsers
  private val categoryCollection = db[JSONCollection]("categories")

  def list = Action.async{
    val cursor = categoryCollection.find(Json.obj()).cursor[Category]()

    cursor.collect[List]().map{ list =>
      Ok(Json.stringify(
        Json.toJson(list.map { category => Category(category.id, category.name, category.imagePath) })
      ))
    }
  }

}
