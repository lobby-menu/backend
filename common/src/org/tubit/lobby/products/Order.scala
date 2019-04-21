package org.tubit.lobby.products

case class Order(
  id: Option[String],
  table: Int,
  bioIdentity: String,
  orders: Seq[SingleBuy],
  creation_date: Long,
  done: Boolean,
  message: Option[String]) {}
