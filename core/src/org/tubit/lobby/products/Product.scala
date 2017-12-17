package org.tubit.lobby.products

/**
  * Definition of a single, barebone product.
  * @param name the name of the product
  * @param categories the categories the product has
  * @param properties the properties that a single instance of this product should hold
  */
case class Product(name: String, categories: Seq[Category], properties: Map[ProductProperty, ProductPropertyValue])
