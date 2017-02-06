package adv

import scala.concurrent.{Future,ExecutionContext}

import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import play.api.mvc.Results.Ok

import java.io.File

import org.scalatest._
import org.scalatest.fixture
import org.scalatest.Matchers._

import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary

import org.typelevel.scalatest._

import store._
import model._
import service._
import controllers._

import scala.io._

import org.scalatest.OptionValues._

/**
 * Pure scalates specification with no Play mess works!
 */
class ControllerSpec extends fixture.FunSpec
  with BeforeAndAfter
  with Matchers { 

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  implicit lazy val app:Application = new TestAppLoader().load(ApplicationLoader.createContext(
    new Environment(new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)))

  type FixtureParam = controllers.Adverts
  def withFixture(test: OneArgTest) = {
    val dba:Dba = new Dynamodb(app.configuration)
    val api:Api[Car] = new service.Adverts(app.configuration,dba)
    val c:controllers.Adverts = new controllers.Adverts(api)
    test(c)
  }

  // preloaded data, not really usefull
  lazy val demo:JsValue = Json.parse(getClass.getClassLoader.getResourceAsStream("data.json"))
  //println(s"demo vals: $demo")
  import GenCars._
  import service.CarAdvertsFormat._

  describe("Web applcation contoller"){

    // val r = route(app, req) // use HttpRequestHandler
    //JsLookupResult = JsDefined | JsUndefined.validationError

    it("index page should be welcome to sailors"){ _ =>
      val controller = new controllers.Index()  
      val result: Future[Result] = controller.index().apply(FakeRequest())
      val body:String = contentAsString(result)
      assert(body.contains("Hello, Sailor."))
    }

    it("should list the adverts"){ ctl =>
      val r:Future[Result] = ctl.list(None,None).apply(FakeRequest())
      println(s"${contentAsString(r)}")
    }

    /* post an advert */
    it("should fail when wrong content-type specified"){ ctl =>
      val req = FakeRequest(POST, "/adverts")
        .withHeaders("Content-Type" -> "some-fake-type")
      assert( status(call(ctl.post, req)) == 415 )
    }

    it("should post new advert"){ ctl =>
      val gen:Gen[Car] = arbitrary[Car]
      val carOpt:Option[Car] = gen.sample
      val car:Car = carOpt.get
      val json = Json.toJson(car)

      val req = FakeRequest(POST, "/adverts")
        .withHeaders("Content-Type"-> "application/json")
        .withJsonBody(json)

      val res = call(ctl.post, req)
      
      status(res) should equal (OK)

      val jsr = contentAsJson(res)

      val id = (jsr \ "id").as[String]

      // check that unique id was generated
      id should not equal car.id

      jsr.validate[Car] match {
        case s:JsSuccess[Car] =>
          val c:Car = s.get 
          info(s"car is returned $c")
        case e:JsError => fail(s"response must be valid $e")
      }
    }
  }
}
