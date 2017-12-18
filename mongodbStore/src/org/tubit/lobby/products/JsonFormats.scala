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


  implicit object SingleBuyReads extends Reads[SingleBuy]{
    override def reads(json: JsValue): JsResult[SingleBuy] = json match {
      case obj : JsObject => try {
        val count = (obj \ "count").as[Int]
        val product = (obj \ "item").as[String]

        JsSuccess(SingleBuy(count, Product(Some(product), "", None, 0, Seq.empty)))
      }catch{
        case cause : Throwable => JsError(cause.getMessage)
      }
      case _ => JsError("expected js.object")
    }
  }

  // table, bioIdentity, orders, creation_date
  implicit object OrderReads extends Reads[Order]{
    override def reads(json: JsValue): JsResult[Order] = json match {
      case obj : JsObject => try {
        val id = (obj \ "_id" \ "$oid").asOpt[String]
        val table = (obj \ "table").as[Int]
        val bioIdentity = (obj \ "bio_identity").as[String]
        val creation_date = (obj \ "creation_date").as[Long]
        val orders = (obj \ "orders").as[Seq[SingleBuy]]
        val done = (obj \ "done").as[Boolean]

        JsSuccess(Order(id, table, bioIdentity, orders, creation_date, done))
      } catch {
        case cause : Throwable => JsError(cause.getMessage)
      }

      case _ => JsError("expected js.object")
    }
  }

  implicit object SingleBuyWrites extends OWrites[SingleBuy]{
    override def writes(single: SingleBuy): JsObject = Json.obj(
      "count" -> single.count,
      "item" -> single.item.id.get
    )
  }

  implicit object OrderWrites extends OWrites[Order]{
    override def writes(order: Order): JsObject =
      order.id
        .map{ id => Json.obj("id" -> id)}
        .getOrElse(Json.obj()) ++ Json.obj(
        "table" -> order.table,
        "bio_identity" -> order.bioIdentity,
        "creation_date" -> order.creation_date,
        "orders" -> Json.toJson(order.orders),
        "done" -> order.done
      )
  }

  implicit object ProductWrites extends OWrites[Product]{
    override def writes(product: Product): JsObject =
      product.id.map{ id =>
        Json.obj("id" -> id )
      }.getOrElse(Json.obj()) ++
      product.imagePath.map{ imagePath =>
        Json.obj("image_path" -> product.imagePath)
      }.getOrElse(Json.obj()) ++ Json.obj(
        "name" -> product.name,
        "categories" -> Json.toJson(product.categories.map(_.name)),
        "price" -> Json.toJson(product.price)
      )
  }

  implicit object ProductReads extends Reads[Product]{
    override def reads(json: JsValue): JsResult[Product] = json match {
      case obj : JsObject => try{
        val id = (obj \ "_id" \ "$oid").asOpt[String]
        val name = (obj \ "name").as[String]
        val imagePath = (obj \ "image_path").asOpt[String]
        val categories = (obj \ "categories").as[Seq[String]]
        val price = (obj \ "price").as[Double]

        JsSuccess(Product(id, name, imagePath, price, categories.map{ name => Category(None, name, "")}))
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
