import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

/**
 * Specs2 app specification. 
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {
    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "work from within a browser" in new WithBrowser {
      browser.goTo("http://localhost:" + port)
      browser.pageSource must contain("Hello, Sailor.")
    }

    // WS(url) here

    "cmon" in new WithServer{
      //running(TestServer(9000)){
        println("?")
      //}
    }
  }
}
