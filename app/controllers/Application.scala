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
  val HomePage = routes.Application.index
  val RecordsPage = routes.Application.records

  def index = Action { implicit rs =>
    Ok(views.html.index())
  }
  
  def records = DBAction { implicit rs =>
    Ok(views.html.records(Query(Records).list))
  }

  val recordForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "date" -> date("dd/MM/yyyy"),
      "dev1" -> text,
      "dev2" -> text,
      "task" -> text
    )(Record.apply)(Record.unapply)
  )

  def insert = DBAction { implicit rs =>
    recordForm.bindFromRequest.fold (
        formWithErrors => {
          Redirect(HomePage).flashing("alert" -> "Enter all values")
        },
        record => {
          Records.insert(record)
          Redirect(HomePage).flashing("alert" -> "Record inserted successfully")
        }
    )            
  }
  
  def delete(id: Long) = DBAction { implicit rs =>
    Records.where(_.id === id).delete
    Redirect(RecordsPage).flashing("alert" -> "Record deleted successfully")
  }  
}