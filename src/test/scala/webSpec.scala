package adv

import scala.concurrent.{Future,ExecutionContext}

import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

import java.io.File

import org.scalatest._
import org.scalatest.fixture

/**
 * Pure scalates specification with no Play mess works!
 */
class ControllerSpec extends fixture.FunSpec
  with Matchers { 

  lazy val app:Application = new TestAppLoader().load(ApplicationLoader.createContext(
    new Environment(new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)))

  type FixtureParam = Application
  def withFixture(test: OneArgTest) = test(app)

  describe("Web applcation"){
    it("index page should be welcome to sailors"){ app =>
      val controller = new controllers.Index()  
      val result: Future[Result] = controller.index().apply(FakeRequest())
        val body:String = contentAsString(result)
        assert(body.contains("Hello, Sailor."))
    }

    it("should call other controllers"){ app =>
      //app.configuration.getString("adv.title") mustBe Some("Car adverts service")
      println(s"${app.configuration.getString("adv.title")}")

    }
  }
}
