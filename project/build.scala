package tiue

import sbt._
import sbt.Keys._
import play.sbt.PlayScala
import play.sbt.PlayLayoutPlugin

object Build extends Build{
  val root = Project(id="scadv", base=file("."), settings=Seq(
    scalaVersion:= "2.11.8",
    resolvers += Resolver.typesafeRepo("releases"),
    libraryDependencies++=Seq(
      // "org.scalaz"      %% "scalaz-core"  % "7.2.8",
      "org.scalacheck"  %% "scalacheck"   % "1.13.4"  % "test",
      "org.scalatest"   %% "scalatest"    % "3.0.1"   % "test"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq("-feature","-deprecation","-language:postfixOps", "-target:jvm-1.8", "-encoding", "UTF-8")
  ))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
}
