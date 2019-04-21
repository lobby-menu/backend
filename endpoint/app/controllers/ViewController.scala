package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, Action, ControllerComponents}

class ViewController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action{
    Ok(views.html.index())
  }

  def details(table: Int) = Action{
    Ok(views.html.details(table))
  }

}
