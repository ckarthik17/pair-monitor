package models

import java.sql.{Date => SqlDate}
import java.util.{Date => UtilDate}

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class Record(id: Option[Long] = None, date: UtilDate, dev1: String, dev2: Option[String], task: String)

@Singleton()
class RecordsDAO @Inject()
(configuration: Configuration, protected val dbConfigProvider: DatabaseConfigProvider)
(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(record: Record): Future[Unit] = {
    db.run(records += record).map(_ => ())
  }

  def delete(recordId: Long): Future[Int] = {
    db.run(records.filter(_.id === recordId).delete)
  }

  def deleteAll: Future[Int] = {
    db.run(records.delete)
  }

  def allPairingSessions: Future[Seq[Record]] = {
    db.run(records.sortBy(r => (r.date.desc, r.dev1)).result)
  }

  def pairingSessions(cutOffDate: UtilDate): Future[Seq[(String, Option[String])]] = {
    db.run(records.filter(r => r.date > cutOffDate).map(r => (r.dev1, r.dev2)).result)
  }


  private val records = TableQuery[Records]

  implicit val localDateColumnType: BaseColumnType[UtilDate] = MappedColumnType.base[UtilDate, SqlDate](
    d => new SqlDate(d.getTime),
    d => new UtilDate(d.getTime)
  )

  class Records(tag: Tag) extends Table[Record](tag, "RECORD") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def date = column[UtilDate]("date")

    def dev1 = column[String]("dev1")

    def dev2 = column[Option[String]]("dev2")

    def task = column[String]("task")

    override def * = (id.?, date, dev1, dev2, task) <> (Record.tupled, Record.unapply)
  }

}