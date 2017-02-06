package adv

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.ws._
import play.api.libs.json._

import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import model._
import service._

import org.specs2.concurrent.ExecutionEnv
import scala.concurrent._
import scala.concurrent.duration._

/**
 * Car adverts web application specification (specs2). 
 *
 * Specify the expected behavior of an application when working throug the web clients and browsers.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification with FutureMatchers { 

  "Adverts application" should {
    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/no_enpoint")) must beSome.which (status(_) == NOT_FOUND)
    }

    "be welcome to the sailors from browser" in new WithBrowser {
      browser.goTo("http://localhost:" + port)
      browser.pageSource must contain("Hello, Sailor.")
    }

    // WS(url) here
    import GenCars._
    import CarAdvertsFormat._

    "post an advert through the web server" in new WithServer{
      // check the service available

      // make the actual random json string here
      val gen:Gen[Car] = arbitrary[Car]
      val carOpt:Option[Car] = gen.sample
      val car:Car = carOpt.get
      // should make the actual random json here
      val advert:JsValue = Json.toJson(car)

      val post:Future[WSResponse] = WS.url("http://localhost:" + port + "/adverts")
        .withHeaders("Content-Type"->"application/json")
        .post(advert)

      val response:WSResponse = Await.result(post, 1 second)

      response.status must equalTo(OK)      
      
      // validate response against json adverts format not the car 
      response.json.validate[Car] match {
        case s:JsSuccess[Car] => success
        case e:JsError        => failure
      }  
    }
  }
}
