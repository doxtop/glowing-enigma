package adv

import org.scalatest.FunSpec
import org.scalatest._
import org.scalatest.Matchers._
import org.scalatest.EitherValues._
import org.scalatest.concurrent._
import org.scalatest.time._
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.waiters._

import collection.JavaConverters._

import play.api.Configuration
import play.api.inject.guice._

import store._

import scalaz._, Scalaz._
import org.typelevel.scalatest._

/** 
 * Test Database applicaiton leyer. 
 * Dba engine configurable in Module or inplace in injector here.
 */
class DbaSpec extends FunSpec 
  with BeforeAndAfter 
  with Matchers 
  with Eventually 
  with SpanSugar 
  with DisjunctionMatchers 
  with DisjunctionValues{

  val injector = new GuiceInjectorBuilder().bindings(new TestModule).injector
  implicit val db = injector.instanceOf[Dba]

  val r = scala.util.Random
  val items = for {
    title <- List("VW Touareg", "Audi A4 Avant")
    fuel <- Seq(Diesel,Gas)
    price = r.nextInt
    neu <- Seq(true, false)
  } yield db.entry(title, fuel, price, neu, None, None)

  // to solve beforeAll puzzle
  before { db.createContainer("test1") }
  after  { db.deleteContainer("test1") }

  // to add assertions later
  describe("test database application") {

    it("describe the table"){ info(s"${db.describe("test1")}")}

    it("should have functionality to add entry"){
      val entry = db.entry("VW Touareg", Diesel, 10, true)

      db.put("test1", entry) should beRight(entry)
    }

    it("should have functionality to return data for single entry by id"){
      val entry = db.entry("VW Touareg", Diesel, 10, true)
      db.put("test1", entry)

      val id = entry.find(_._1 == "id")
      id should be ('defined)

      val pk = id.get._2.asInstanceOf[AttributeValue].getS

      db.get("test1", pk) should beRight(entry)
    }

    it("shoud have functionality to delete entry by id") {
      val entry = db.entry("VW Golf", Diesel, 10, true)
      val e = db.put("test1", entry)
      val id = entry.find(_._1 == "id")
      val pk = id.get._2.asInstanceOf[AttributeValue].getS // hide in store
      
      db.delete("test1", pk) should beRight(entry)
      
      db.get("table1", pk) should be ('left)
    }

    it("shoud have functionality to modify entry by id") {
      val entry = db.entry("VW Touareg", Diesel, 10, true)
      db.put("test1", entry)
      val id = entry.find(_._1 == "id")
      val pk = id.get._2.asInstanceOf[AttributeValue].getS // hide in store
      
      val upd = db.entry("VW W12", Gas, 10, false) + ("id" -> id.get._2)

      db.put("test1", upd)

      val check = db.get("test1", pk)
      
      check should beRight(upd) 

      // just demo
      import PartialFunctionValues._
      check.value.valueAt("fuel") should be (new AttributeValue("Gas"))
      check.value.valueAt("title") should be (new AttributeValue("VW W12"))
      check.value.valueAt("new") should be (new AttributeValue().withBOOL(false))
    }

    it("should have functionality to return list of all entries") {
      db.entries("test1").value should be(empty)
      

      println(s"Size:${items.size}")
      val e1 = db.entry("VW Touareg", Diesel, 10, true)
      db.put("test1", e1)

      val e2 = db.entry("VW Touareg", Diesel, 10, true)
      db.put("test1", e2)

      val e3 = db.entry("VW Touareg", Diesel, 10, true)
      db.put("test1", e3)

      eventually (timeout(2 seconds), interval(200 millis)){ 
        db.entries("test1").value.size should equal(3)
        db.entries("test1").value should contain (e1)
        db.entries("test1").value should contain (e3)
      }

    }
  }

}

