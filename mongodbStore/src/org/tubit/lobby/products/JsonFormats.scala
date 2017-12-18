package org.tubit.lobby.products

object JsonFormats {
  import play.api.libs.json._

  implicit object CategoryWrites extends OWrites[Category]{
    override def writes(category: Category): JsObject =
      category.id.map{ id =>
        Json.obj("id" -> id )
      }.getOrElse(Json.obj()) ++ Json.obj(
        "name" -> category.name,
        "image_path" -> category.imagePath
      )
  }

  implicit object CategoryReads extends Reads[Category]{
    override def reads(json: JsValue): JsResult[Category] = json match {
      case obj : JsObject => try{
        System.out.println(obj)
        val id = (obj \ "_id" \ "$oid").asOpt[String]
        val name = (obj \ "name").as[String]
        val imagePath = (obj \ "image_path").as[String]

        JsSuccess(Category(id, name, imagePath))
      } catch {
        case cause: Throwable => JsError(cause.getMessage)
      }

    case _ => JsError("expected js.object")
    }
  }

  implicit object JsObjectWrites extends OWrites[JsObject]{
    override def writes(value: JsObject): JsObject = value
  }

  implicit object JsObjectReads extends Reads[JsObject]{
    override def reads(json: JsValue): JsResult[JsObject] = json match {
      case obj : JsObject => JsSuccess(obj)
      case _ => JsError("expected js.object")
    }
  }
}
