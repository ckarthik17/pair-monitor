package models

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import java.util.Date
import slick.lifted.MappedTypeMapper
import play.api.Play
import play.api.Play.current

case class Record(id: Option[Long] = None, date: Date, dev1: String, dev2: Option[String], task: String)

object Records extends Table[Record]("RECORD") {
  implicit val javaUtilDateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
    x => new java.sql.Date(x.getTime),
    x => new java.util.Date(x.getTime)
  )

	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def date = column[Date]("date", O.NotNull)
  def dev1 = column[String]("dev1", O.NotNull)
  def dev2 = column[String]("dev2", O.Nullable)
  def task = column[String]("task", O.NotNull)

  def * = id.? ~ date ~  dev1 ~ dev2.? ~ task <> (Record, Record.unapply _)
  
  def pairCount(dev: String) = {
    DB.withSession { implicit session:Session =>
      val records = Query(Records).where(r => (r.dev1 === dev || r.dev2 === dev)).list
      val pairs = for(record <- records) yield (record.dev1, record.dev2)
      val pairedWithList = pairs.map { p =>        
        val d1 = p._1
        val d2 = p._2
        if(d1 == dev) {
          d2 match {
            case None => dev
            case Some("") => dev
            case _ => d2.get
          }
        } 
        else d1
      } 
      val pairedWithCount = pairedWithList.groupBy(identity).mapValues(_.size).withDefaultValue(0)
      val result = for(d <- devs) yield pairedWithCount(d)
      result.mkString("['" + dev + "',",",","]")
    }    
  }

  def topPairs(n: Integer) = {
    DB.withSession { implicit session:Session =>
      val records = Query(Records).list
      val pairs = for(record <- records) yield (record.dev1, 
        record.dev2 match { 
          case None => record.dev1
          case Some(x) => x
        }
      )
      val consolidatedPairs = pairs.map { p =>
        val (k,v) = p
        if(k < v) (k,v) else (v,k)
      }

      val pairCountMap = consolidatedPairs.groupBy(identity).mapValues(_.size).withDefaultValue(0)
      pairCountMap.toSeq.sortBy(x => -(x._2)).take(n)
    }        
  }

  def devs = {
    Play.current.configuration.getString("devs").get.split(",")
  }
}