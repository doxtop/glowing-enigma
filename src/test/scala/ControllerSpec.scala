package adv

import scala.concurrent.{Future,ExecutionContext}

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatestplus.play._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import play.api.inject._
import play.api.inject.guice._

import controllers._

import play.api.inject.guice.GuiceApplicationBuilder

import scala.language.implicitConversions

class ControllerSpec extends PlaySpec with Results with ScalaFutures {

  // implicit override lazy val app = new GuiceApplicationBuilder()
  //   .bindings(new PlayTestModule)
  //   .build

  // "Example Page#get" should {

  //   "should be valid" in {
  //     val controller = app.injector.instanceOf[HomeController]  
  //     val result: Future[Result] = controller.get().apply(FakeRequest())
  //     val bodyText: String = contentAsString(result)
  //     bodyText mustBe "ok"
  //   }
  // }
}
