package tiue

import sbt._
import sbt.Keys._

object Build extends Build{
  val root = Project(id="scadv", base=file("."), settings=Seq(
    scalaVersion:= "2.12.1",
    libraryDependencies++=Seq(
      "org.scalaz"      %% "scalaz-core"  % "7.2.8",
      "org.scalacheck"  %% "scalacheck"   % "1.13.4"  % "test",
      "org.scalatest"   %% "scalatest"    % "3.0.1"   % "test"
    ),
    scalacOptions ++= Seq("-feature","-deprecation","-language:postfixOps")
  ))
}
