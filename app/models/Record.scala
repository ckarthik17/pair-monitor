package models

import play.api.db.slick.Config.driver.simple._
import java.util.Date
import slick.lifted.MappedTypeMapper

case class Record(date: Date, dev1: String, dev2: String, task: String)

object Records extends Table[Record]("RECORD") {
  implicit val javaUtilDateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
    x => new java.sql.Date(x.getTime),
    x => new java.util.Date(x.getTime)
  )

  def date = column[Date]("date", O.NotNull)
  def dev1 = column[String]("dev1", O.NotNull)
  def dev2 = column[String]("dev2", O.NotNull)
  def task = column[String]("task", O.NotNull)

  def * = date ~  dev1 ~ dev2 ~ task <> (Record.apply _, Record.unapply _)
}