import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "pair_monitor"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(    
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(    
  	scalaVersion := "2.10.2"
  ).dependsOn(RootProject(file("/Users/ckarthik/code/play-slick")))

}
