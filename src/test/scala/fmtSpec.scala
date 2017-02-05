package adv

import org.scalatest._
import org.scalatest.prop._
import org.scalacheck._, Gen._
import org.scalacheck.util._
import org.scalacheck.Arbitrary.arbitrary

import play.api.libs.json._
import model._

object GenCars {
  // generate samlpe data
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
}

class FormatSpec extends FunSpec with GeneratorDrivenPropertyChecks with Matchers with Checkers {
  import GenCars._

  describe("property"){
    it("obvious"){
      forAll { (a:String,b:String) => 
        a.length + b.length should equal ((a+b).length)
      }    
    }  
  }

  describe("sample the generators") {
    it("just info"){
      println(s"s1 :${newCars.sample}")
      println(s"s2 :${usedCars.sample}")
      println(s"s3 :${newCars.sample}")
      println(s"s1 :${usedCars.sample}")
      println(s"s2 :${newCars.sample}")
      println(s"s3 :${usedCars.sample}")
    }
  }

}

