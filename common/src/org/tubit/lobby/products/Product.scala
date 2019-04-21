package org.tubit.lobby.products

/**
  * Definition of a single, barebone product.
  * @param name the name of the product
  * @param imagePath the path of the product image.
  * @param categories the categories the product has
  */
case class Product(id: Option[String], name: String, imagePath: Option[String], price: Double, categories: Seq[Category], description: Option[String])
