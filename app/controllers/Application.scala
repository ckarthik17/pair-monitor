package controllers

import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current

object Application extends Controller{

  def index = DBAction { implicit rs =>
    Ok(views.html.index(Query(Records).list))
  }

  val recordForm = Form(
    mapping(
      "date" -> date("dd/MM/yyyy"),
      "dev1" -> text,
      "dev2" -> text,
      "task" -> text
    )(Record.apply)(Record.unapply)
  )

  def insert = DBAction { implicit rs =>
    recordForm.bindFromRequest.fold (
        formWithErrors => BadRequest( "You need to pass all values!" ),
        record => Records.insert(record)
    )        
    Redirect(routes.Application.index)
  }
  
}