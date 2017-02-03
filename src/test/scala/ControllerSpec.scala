package adv

import scala.concurrent.{Future,ExecutionContext}
import org.scalatestplus.play._

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import play.api.inject.guice._

import controllers._
import service._

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest._
import org.scalatest.concurrent._

class ControllerSpec(implicit ec:ExecutionContext) extends PlaySpec with Results with ScalaFutures {

  val injector = new GuiceInjectorBuilder().bindings(new TestModule).injector
  
  "Example Page#get" should {
    "should be valid" in {
      val controller = injector.instanceOf[HomeController]
      val result: Future[Result] = controller.get().apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "ok"
    }
  }
}
