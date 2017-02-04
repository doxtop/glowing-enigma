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
import store.{Entry=>E}
import model._

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

  // to solve beforeAll puzzle
  before { db.createContainer("test1") }
  after  { db.deleteContainer("test1") }

  def demo():E = Map(
    "id"    -> St(db.nextId),
    "title" -> St("VW Touareg"),
    "fuel"  -> Fl(Diesel),
    "price" -> In(100),
    "new"   -> Bl(true)
  )

  def demo2():E = Map(
    "id"    -> St(db.nextId),
    "title" -> St("VW Golf"),
    "fuel"  -> Fl(Gas),
    "price" -> In(1000),
    "new"   -> Bl(true)
  )

  // to add assertions later
  describe("test database application") {

    it("describe the table"){ info(s"${db.describe("test1")}")}

    it("should have functionality to add entry"){
      val entry = demo()

      db.put("test1", entry) should beRight(entry)
    }

    it("should have functionality to return data for single entry by id"){
      val entry = demo()

      db.put("test1", entry) should beRight(entry)

      val id = entry.find(_._1 == "id")
      id should be ('defined)

      id.get._2 match {
        case St(pk) => 
          db.get("test1", pk) should beRight(entry)
        case _      => 
          fail(s"wrong key attribute $id")
      }
    }

    it("should fail on non existent table") {
      val fake_table = "fake_table_x"  
      
      val delfail = db.del(fake_table, "") 
      
      delfail should be ('left)
      delfail.leftMap(_ match {
        case NotFound(s) => succeed
        case other => fail(s"not expected fail $other")
      })
    }

    it("should warn when item is not exist"){
      val fake_id = "iamnotexist"

      val delfail = db.del("test1", fake_id)
      delfail should be ('left)
      delfail.leftMap(_ match {
        case NotFound(s) => succeed
        case other => fail(s"not expected fail $other")
      })
    }

    it("shoud have functionality to delete entry by id") {
      val entry = demo()
      db.put("test1", entry) should beRight(entry)

      val id = entry.find(_._1 == "id")
      id should be ('defined)

      id.get._2 match {
        case St(pk) => 
          db.del("test1", pk) should beRight(entry)
          db.get("test1", pk) should be ('left)    
        case _ =>
          fail(s"wrong key attribute $id")
      }      
    }

    it("shoud have functionality to modify entry by id") {
      val entry = demo()
      db.put("test1", entry)

      val id = entry.find(_._1 == "id")
      id should be ('defined)

      id.get._2 match {
        case St(pk) =>
          val upd = demo2() + ("id" -> id.get._2)

          db.put("test1", upd)

          val check = db.get("test1", pk)
          check should beRight(upd) 

          import PartialFunctionValues._
          check.value.valueAt("fuel") should be (Fl(Gas))
          check.value.valueAt("title") should be (St("VW Golf"))
          check.value.valueAt("new") should be (Bl(true))

        case _ => 
          fail(s"wrong key attribute $id")
      }
    }

    ignore("should have functionality to return list of all entries") {
      db.entries("test1").value should be(empty)
      
      val e1 = demo()
      db.put("test1", e1)

      val e2 = demo()
      db.put("test1", e2)

      val e3 = demo()
      db.put("test1", e3)

      eventually (timeout(2 seconds), interval(200 millis)){ 
        db.entries("test1").value.size should equal(3)
        db.entries("test1").value should contain (e1)
        db.entries("test1").value should contain (e3)
      }

    }
  }

}

