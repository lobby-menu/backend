package org.tubit.lobby.products

sealed trait ProductPropertyValue

object ProductPropertyValue{
  case object Boolean extends ProductPropertyValue
  case object Enum extends ProductPropertyValue
  case object Numerical extends ProductPropertyValue

  def apply(ppValue: String) : Either[String, ProductPropertyValue] = ppValue match {
    case "boolean" => Right(ProductPropertyValue.Boolean)
    case "enum" => Right(ProductPropertyValue.Enum)
    case "numerical" => Right(ProductPropertyValue.Numerical)
    case _ => Left(s"Unknown ProductPropertyValue for the given string: $ppValue")
  }

  def unapply(ppValue: String) : Option[ProductPropertyValue] = apply(ppValue).right.toOption
}
