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
  val HomePage = routes.Application.chart
  val InsertPage = routes.Application.insert
  val RecordsPage = routes.Application.records

  def insert = Action { implicit rs =>
    Ok(views.html.insert())
  }
  
  def chart = Action { implicit rs =>
    Ok(views.html.chart())
  }
  
  def records = DBAction { implicit rs =>
    val query = Query(Records).sortBy(r => (r.date.desc,r.dev1))
    Ok(views.html.records(query.list))
  }
  
  def deleteAll = DBAction { implicit rs =>
    val query = Query(Records)
    query.delete
    Ok(views.html.records(query.list))
  }

  val recordForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "date" -> date("dd/MM/yyyy"),
      "dev1" -> text,
      "dev2" -> optional(text),
      "task" -> text
    )(Record.apply)(Record.unapply)
  )

  def newRecord = DBAction { implicit rs =>
    recordForm.bindFromRequest.fold (
        formWithErrors => {
          Redirect(RecordsPage).flashing("alert-error" -> "Enter proper values")
        },
        record => {
          Records.insert(record)
          Redirect(RecordsPage).flashing("alert-success" -> "Record inserted successfully")
        }
    )            
  }
  
  def delete(id: Long) = DBAction { implicit rs =>
    Records.where(_.id === id).delete
    Redirect(RecordsPage).flashing("alert-success" -> "Record deleted successfully")
  }  
}