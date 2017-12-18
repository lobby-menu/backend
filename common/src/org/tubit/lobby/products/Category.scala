package org.tubit.lobby.products

/**
  * Category that can be attached to products
  * @param name
  * @param imagePath
  */
case class Category(id: Option[String], name: String, imagePath: String)
