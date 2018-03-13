package controllers

import java.util.{Calendar, Date}

import javax.inject.{Inject, Singleton}
import models.{Record, RecordsDAO}
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Application @Inject()
(cc: ControllerComponents,
 configuration: Configuration,
 recordsDAO: RecordsDAO
)
  extends AbstractController(cc) {

  private val RecordsPage = routes.Application.records
  private val recordForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "date" -> date("dd/MM/yyyy"),
      "dev1" -> text,
      "dev2" -> optional(text),
      "task" -> text
    )(Record.apply)(Record.unapply)
  )

  val allDevs: List[String] = configuration.get[String]("devs").split(",").toList

  def insert = Action { implicit request =>
    Ok(views.html.insert(allDevs))
  }

  def chart = Action.async { implicit request =>
    val cutOffDate: Date = {
      val cal = Calendar.getInstance()
      cal.add(Calendar.MONTH, -1)
      cal.getTime
    }
    val chartDataF: Future[(String, List[((String, String), Int)])] = recordsDAO.pairingSessions(cutOffDate).map {
      records: Seq[(String, Option[String])] => {
        val pairingData = allDevs.map { dev =>
          val pairedWithList: Seq[String] = records.collect {
            case (d1, Some(d2)) if d1 == dev || d2 == dev => if (d1 != dev) d1 else d2
            case (d1, None) if d1 == dev => d1
          }
          val pairingCount: Map[String, Int] = pairedWithList.groupBy(identity).mapValues(_.size).withDefaultValue(0)
          allDevs.map(d => pairingCount(d)).mkString("['" + dev + "',", ",", "]")
        }.mkString(",")

        val mostPairedDevs = records.map {
          case (dev1, Some(dev2)) => if (dev1 < dev2) (dev1, dev2) else (dev2, dev1)
          case (dev1, None) => (dev1, dev1)
        }.groupBy(identity).mapValues(_.size).toList.sortBy(-_._2).take(3)
        (pairingData, mostPairedDevs)
      }
    }

    for {
      (pairingData, mostPairedDevs) <- chartDataF
    } yield Ok(views.html.chart(allDevs, pairingData, mostPairedDevs))
  }

  def records = Action.async { implicit request =>
    recordsDAO.allPairingSessions.map(pairingSession => Ok(views.html.records(pairingSession.toList, allDevs)))
  }

  def newRecord = Action.async { implicit request =>
    recordForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(Redirect(RecordsPage).flashing("alert-error" -> "Enter proper values"))
      },
      record => {
        recordsDAO.insert(record).map(_ => Redirect(RecordsPage).flashing("alert-success" -> "Record inserted successfully"))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit rs =>
    recordsDAO.delete(id).map { noOfRows =>
      if (noOfRows > 0)
        Redirect(RecordsPage).flashing("alert-success" -> "Record deleted successfully")
      else
        Redirect(RecordsPage).flashing("alert-failure" -> "Record Id not found.")
    }

  }

  def deleteAll = Action.async { implicit request =>
    recordsDAO.deleteAll.map(_ => Ok(views.html.records(List.empty[Record], allDevs)))
  }

}
