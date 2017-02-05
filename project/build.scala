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
      ("com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.86")
        .exclude("com.amazonaws", "aws-java-sdk-kms")
        .exclude("com.amazonaws", "aws-java-sdk-s3")
        .exclude("software.amazon.ion", "ion-java"),
      "org.scalaz"      %% "scalaz-core"  % "7.2.8",
      "org.scalacheck"  %% "scalacheck"   % "1.13.4"  % "test",
      "org.scalatest"   %% "scalatest"    % "3.0.1"   % "test",
      "org.typelevel" %% "scalaz-scalatest" % "1.1.0" % "test",
      "com.storm-enroute" %% "scalameter" % "0.7",

    // scalatestplusplay doesn't match correctly with play
    // and brake even specs2 tests 1.5.1 or 2.0.0-M1 doesn't metter
    // so any play related functionality should be tested with specs2

      ("com.typesafe.play" %% "play-specs2" % "2.4.8" % "test")
        .excludeAll(ExclusionRule(organization = "org.specs2")),
      "org.specs2" %% "specs2-core" % "3.7" % "test",
      "org.specs2" %% "specs2-junit" % "3.7" % "test"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq("-feature","-deprecation","-language:postfixOps", "-target:jvm-1.8", "-encoding", "UTF-8"),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Benchmark := false,
    logBuffered := false,
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
  ))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .configs (
    Benchmark
  ).settings (
    inConfig(Benchmark)(Defaults.testSettings): _*
  )
}
