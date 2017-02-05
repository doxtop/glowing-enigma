package tiue

import sbt._
import sbt.Keys._
import play.sbt.PlayScala
import play.sbt.PlayLayoutPlugin
import play.sbt.routes.RoutesKeys._

object Build extends Build{
  lazy val Benchmark = config("bench") extend Test

  val root = Project(id="scadv", base=file("."), settings=Seq(
    scalaVersion:= "2.11.8",
    resolvers ++= Seq(Resolver.typesafeRepo("releases"), 
      "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/releases"),
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies++=Seq(
      "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.86",
      "org.scalaz"      %% "scalaz-core"  % "7.2.8",
      "org.scalacheck"  %% "scalacheck"   % "1.13.4"  % "test",
      "org.scalatest"   %% "scalatest"    % "3.0.1"   % "test",
      "org.typelevel" %% "scalaz-scalatest" % "1.1.0" % "test",
      "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % "test",
      "com.storm-enroute" %% "scalameter" % "0.7"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq("-feature","-deprecation","-language:postfixOps", "-target:jvm-1.8", "-encoding", "UTF-8"),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Benchmark := false,
    logBuffered := false
  ))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .configs (
    Benchmark
  ).settings (
    inConfig(Benchmark)(Defaults.testSettings): _*
  )
}
