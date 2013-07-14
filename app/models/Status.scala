package models

import java.sql.Date
import play.api.db.slick.Config.driver.simple._

case class Status(date: Date, dev1: String, dev2: String, task: String)

object Statuses extends Table[Status]("STATUS") {

  def date = column[Date]("date", O.NotNull)
  def dev1 = column[String]("dev1", O.NotNull)
  def dev2 = column[String]("dev2", O.NotNull)
  def task = column[String]("task", O.NotNull)

  def * = date ~ dev1 ~ dev2 ~ task <> (Status.apply _, Status.unapply _)
}