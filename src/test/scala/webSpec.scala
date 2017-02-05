package adv

import scala.concurrent.{Future,ExecutionContext}

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatestplus.play._

import play.api.{Play, Application}
import play.api.routing.sird._
import play.api.routing._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.test._

import play.api.inject.guice.GuiceApplicationBuilder

import controllers._

class ControllerSpec extends PlaySpec with Results with ScalaFutures with OneAppPerSuite {

  implicit override lazy val app = new GuiceApplicationBuilder()
    .bindings(new PlayTestModule)
    .configure(Map("s" -> "s"))
    .router(Router.from{
      case GET(p"/test") => 
        Action(Results.Ok("fucking test"))
    })
    .build

  "The OneAppPerSuite trait" must {
    "provide a FakeApplication" in {
      app.configuration.getString("s") mustBe Some("s")
    }
  }

}
