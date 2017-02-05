package adv

//import javax.inject._
import scala.concurrent.{Future,ExecutionContext}

import org.scalatest._
import org.scalatestplus.play._
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.test._
import java.io.File

import org.scalatest.fixture

class ControllerSpec // extends PlaySpec
  extends fixture.FunSpec
  with Matchers
  //with BaseOneAppPerTest
  //with OneAppPerTest - di
  //with OneAppPerSuite - di
  //with FakeApplicationFactory
    { this:TestSuite =>

  type FixtureParam = Application

  override def withFixture(test: NoArgTest) = {
    test()
  }

  def withFixture(test: OneArgTest) = {
    test(app)
  }

  lazy val app:Application = new TestAppLoader().load(ApplicationLoader.createContext(
    new Environment(new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)))

    import play.api.test.Helpers._

    describe("Play application"){
      it("should fail with non injected testing support completely"){ app =>
        intercept[java.lang.AbstractMethodError] {
          val controller = new controllers.Index()  
          val result: Future[Result] = controller.index().apply(FakeRequest())
          val body:String = contentAsString(result)
          body shouldBe("ok")
        }
      }    
    }
}
