package adv

import org.scalatest.FunSpec
import org.scalatest._

import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.waiters._

import collection.JavaConverters._

import play.api.Configuration
import play.api.inject.guice._

import store._

/*
  just go through dynamodb guideline
  skip that suite/beforeandafterall mess, its not relevant
  ignore all limitation for a moment
  json doc api provided, but lets try with attributes first
*/
class KvsSpec extends FunSpec with BeforeAndAfter {
  val injector = new GuiceInjectorBuilder().bindings(new TestModule).injector
  implicit val db = injector.instanceOf[Dba]

  // db atttibutes
  type Advert = Map[String, AttributeValue]

  // demo data
  def uuid() = java.util.UUID.randomUUID.toString.replace("-","")

  def item(title:String, fuel:Fuel, price:Int, 
    neu:Boolean, mileage:Option[Int]=None, reg:Option[String]=None):Advert = Map(
    "id" -> Attribute(uuid),
    "title" -> Attribute(title),
    "fuel" -> Attribute(fuel),
    "price" -> Attribute(price),
    "new" -> Attribute(neu),
    "mileage" -> Attribute(100),
    "registration" -> Attribute("01-01-1970")
  )

  val r = scala.util.Random
  val ts = List("VW Touareg", "Audi A4 Avant")
  val fls= Seq(Diesel,Gas)

  val items:List[Advert] = for {
    title <- ts
    fuel <- fls
    price = r.nextInt
    neu <- Seq(true, false)
  } yield item(title, fuel, price, neu)

  // to solve beforeAll puzzle
  before { println(s"${db.createContainer("test1")}") }
  after  { println(s"${db.deleteContainer("test1")}") }

  // to add assertions later
  describe("test store") {
    it("describe the table"){
//      info(s"Limits:  ${db.describeLimits(new DescribeLimitsRequest)}")
//      info(s"Table:   ${db.describeTable("test1")}")
    }

    ignore("should have functionality to add car advert;"){
      // items
      //   .map(it=>db.putItem(new PutItemRequest("test1",it.asJava)))
      //   .map(r=>info(s"$r"))
    }

    it("should have functionality to return list of all car adverts") (pending)

    ignore("should have default sorting by **id**") {
      //items.map(it => db.putItem(new PutItemRequest("test1",it.asJava)))

      // so its just need to have the gsi, all data in table same value, 
      // will not scale at all.

      // 1000 writes, 3000 reads, 10GB size
      // use index, B.hash = values, B.range = id
      //projection doens't go to table
      
      val query:QueryRequest = new QueryRequest("test1")
        .withKeyConditionExpression("id = :id1")
        .withExpressionAttributeValues(Map(":id1" -> Attribute("sss")).asJava)
        //.set ScanIndexForward: - for sort key

      //info(s"${db.query(query)}")
    }

    it("should have sorting by **title**") (pending)
    it("should have sorting by **fuel**") (pending)
    it("should have sorting by **price**") (pending)
    it("should have sorting by **new**") (pending)
    it("should have sorting by **mileage**") (pending)
    it("should have sorting by **first registration**") (pending)
    
    it("should have functionality to return data for single car advert by id;")(pending)
    it("shoud have functionality to modify car advert by id;") (pending)
    it("shoud have functionality to delete car advert by id;") (pending)

    ignore ("scan for return list of all car adverts") {
      // Scan read every item in table or secondary index.
      // Can't guaranty default sorting order by *id*
      // Only filter and projection here
      // sort after receive all elements?
      // items.map{ i =>
      //   val pr :PutItemRequest  = new PutItemRequest("test1", i.asJava)
      //   val pre:PutItemResult   = db.putItem(pr)
      // }
      val scanRequest:ScanRequest = new ScanRequest("test1")
      //info(s"${db.scan(scanRequest)}")
    }

  }

}

