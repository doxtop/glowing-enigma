package adv

import org.scalatest._
import org.scalatest.prop._
import org.scalacheck._, Gen._
import org.scalacheck.util._
import org.scalacheck.Arbitrary.arbitrary

import play.api.libs.json._
import play.api.libs.functional.syntax._
import model._

/**
 * generate samlpe data
 */ 
object GenCars {
  
  import java.time._
  import java.time.format._

  val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit val d:Arbitrary[LocalDate] = Arbitrary(Gen.choose(0L, Long.MaxValue/3000000) // take next 50 years
    .map(Instant.ofEpochMilli(_).atZone(ZoneId.systemDefault()).toLocalDate))

  def newCars:Gen[Car] = for {
    id      <- Gen.uuid.map(_.toString.replace("-",""))
    title   <- Gen.listOfN(10, Gen.alphaChar).map(_.mkString)
    fuel    <- Gen.oneOf(Diesel,Gas)
    price   <- Gen.posNum[Int]
    } yield Car(id,title,fuel,price)

  def usedCars:Gen[Car] = for {
    id      <- Gen.uuid.map(_.toString.replace("-",""))
    title   <- Gen.listOfN(10, Gen.alphaChar).map(_.mkString)
    fuel    <- Gen.oneOf(Diesel,Gas)
    price   <- Gen.posNum[Int]
    mileage <- Gen.some(Gen.posNum[Int])
    reg     <- Gen.some(arbitrary[LocalDate].map(_.format(fmt)))
    } yield Car(id,title,fuel,price,false,mileage,reg)

  implicit val ac:Arbitrary[Car] = Arbitrary(Gen.oneOf(usedCars, newCars))
}

class FormatSpec extends FunSpec 
  with Matchers
  with Checkers
  with GeneratorDrivenPropertyChecks {
  import GenCars._
  import service.CarAdvertsFormat._

  describe("play json library test :)"){
    it("should be able to parse the car"){
      forAll ("car") { c:Car =>
        val str = Json.toJson(c).toString
        c == (Json.parse(str).as[Car])
        info(s"$str")
      }    
    }  
  }

}

