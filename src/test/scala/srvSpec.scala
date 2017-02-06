package adv

import org.scalatest._

import com.google.inject._

import com.google.inject.name.Names
import play.api.inject._
import play.api.inject.guice._

import service._
import model._

import org.scalatest.Matchers._
import org.scalatest.EitherValues._
import org.typelevel.scalatest._

/**
 * Adverts service layer specification.
 *
 * Sorting adverts list functionality.
 */
class SrvSpec extends FunSpec with Matchers{
  
  val injector = new GuiceInjectorBuilder().bindings(new TestModule).injector
  val qualifier = Some(QualifierInstance(Names.named("car")))
  val bindingKey = BindingKey[Api[Car]](classOf[Api[Car]], qualifier)
  implicit val srv:Api[Car] = injector.instanceOf[Api[Car]](bindingKey)

  describe ("service layer") {
    it("should have database initialized") {
      val i = srv.init 
      i should be ('right)
      info(s"$i")
    }

    it ("proxy every operatoin to dba") (pending)
    it ("various list sort check") (pending)
    it("post car adverts"){}
  }  
}
