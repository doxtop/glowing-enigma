package adv

import org.scalatest._
//import org.scalacheck._

class AdvSpec extends FlatSpecLike {
  behavior of "Adv Servce"

  //not the actual test just help to run in dev mode
  it should "help to run" in {
    info(s"hello, sailor")
  }
}
