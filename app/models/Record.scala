package models

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import java.util.Date
import slick.lifted.MappedTypeMapper
import play.api.Play
import play.api.Play.current

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
  
  def pairCount(dev: String) = {
    DB.withSession { implicit session:Session =>
      val records = Query(Records).where(r => (r.dev1 === dev || r.dev2 === dev)).list
      val pairs = for(record <- records) yield (record.dev1, record.dev2)
      val pairedWithList = pairs.map { p =>        
        if(p._1 == dev) { 
          if(p._2 == "") dev else p._2
        } 
        else p._1
      } 
      val pairedWithCount = pairedWithList.groupBy(identity).mapValues(_.size).withDefaultValue(0)
      val result = for(d <- devs) yield pairedWithCount(d)
      result.mkString("['" + dev + "',",",","]")
    }    
  }

  def devs = {
    Play.current.configuration.getString("devs").get.split(",")
  }
}