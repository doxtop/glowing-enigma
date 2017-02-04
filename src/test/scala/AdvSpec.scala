package adv

import org.scalatest._
import play.api.inject.guice._
import play.api.inject._

import com.google.inject._
import com.google.inject.name.Names

import service._
import model._
import java.lang.Class

class AdvSpec extends FlatSpecLike {
  behavior of "Adv Servce"

  val injector = new GuiceInjectorBuilder().bindings(new TestModule).injector
  val qualifier = Some(QualifierInstance(Names.named("car")))
  val bindingKey = BindingKey[Api[Car]](classOf[Api[Car]], qualifier)

  implicit val srv:Api[Car] = injector.instanceOf[Api[Car]](bindingKey)

  //not the actual test just help to run in dev mode
  it should "help to run" in {
    info(s"hello, sailor")
  }
}
