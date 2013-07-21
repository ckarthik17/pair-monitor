package models

import play.api.db.slick.Config.driver.simple._
import java.util.Date
import slick.lifted.MappedTypeMapper
import play.api.Play

case class Record(id: Option[Long] = None, date: Date, dev1: String, dev2: String, task: String)

object Records extends Table[Record]("RECORD") {
  implicit val javaUtilDateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
    x => new java.sql.Date(x.getTime),
    x => new java.util.Date(x.getTime)
  )

	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def date = column[Date]("date", O.NotNull)
  def dev1 = column[String]("dev1", O.NotNull)
  def dev2 = column[String]("dev2", O.NotNull)
  def task = column[String]("task", O.NotNull)

  def * = id.? ~ date ~  dev1 ~ dev2 ~ task <> (Record.apply _, Record.unapply _)
  
  def getDevs = {
    Play.current.configuration.getString("devs").get.split(",")
  }
}