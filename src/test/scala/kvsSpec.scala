package adv

import org.scalatest.FunSpec
import org.scalatest._

import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB,AmazonDynamoDBClientBuilder}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.document._
import com.amazonaws.waiters._

import collection.JavaConverters._

/*
  just go through dynamodb guideline
  skip that suite/beforeandafterall mess, its not relevant
  ignore all limitation for a moment
  json doc api provided, but lets try with attributes first
*/
class KvsSpec extends FunSpec with BeforeAndAfter {
  org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
     .asInstanceOf[ch.qos.logback.classic.Logger]
     .setLevel(ch.qos.logback.classic.Level.WARN)

  val b:AmazonDynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard
  val endpoint:EndpointConfiguration = new EndpointConfiguration("http://localhost:8000", "")
  val db:AmazonDynamoDB = b.withEndpointConfiguration(endpoint).build

  // pre model
  sealed trait Fuel
  case object Gas extends Fuel
  case object Diesel extends Fuel
  
  case class Car(
    id:Int, 
    title:String,
    fuel:Fuel,
    price:Int,
    neu:Boolean,
    mileage:Option[Int],
    reg:Option[String])

  // db atttibutes
  type Advert = Map[String, AttributeValue]

  // wrap some aws sdk
  object Attribute {
    def apply(s:Any) = s match {
      case st:String  => new AttributeValue(st)
      case i:Int      => new AttributeValue().withN(i.toString)
      case b:Boolean  => new AttributeValue().withBOOL(b)
      case f:Fuel     => new AttributeValue(f.toString) // scala issue for type check
      case _          => new AttributeValue().withNULL(true)
    }
  }

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
  before {
    val request:CreateTableRequest = new CreateTableRequest()
      .withAttributeDefinitions(new AttributeDefinition("id", ScalarAttributeType.S))
      .withKeySchema(new KeySchemaElement("id", KeyType.HASH))
      .withProvisionedThroughput(new ProvisionedThroughput(1.toLong, 1.toLong))
      .withTableName("test1")

    val t:CreateTableResult = db.createTable(request)
    val w = db.waiters().tableExists()
    w.run(new WaiterParameters(new DescribeTableRequest("test1")))
  }

  after {
    val req:DeleteTableRequest = new DeleteTableRequest("test1")
    val d:DeleteTableResult = db.deleteTable(req)
    val w = db.waiters().tableNotExists()
    w.run(new WaiterParameters(new DescribeTableRequest("test1")))
  }

  // to add assertions later
  describe("test store") {
    it("describe the table"){
      info(s"Limits:  ${db.describeLimits(new DescribeLimitsRequest)}")
      info(s"Table:   ${db.describeTable("test1")}")
    }

    it("should have functionality to add car advert;"){
      items
        .map(it=>db.putItem(new PutItemRequest("test1",it.asJava)))
        .map(r=>info(s"$r"))
    }

    it("should have functionality to return list of all car adverts") {
      items.map{ i=>
        val pr :PutItemRequest  = new PutItemRequest("test1", i.asJava)
        val pre:PutItemResult   = db.putItem(pr)
      }

      // val condition:Condition = new Condition()
      //   .withComparisonOperator(ComparisonOperator.GT.toString)
      //   .withAttributeValueList(new AttributeValue().withN(""));
      // val scanFilter = Map("" -> condition)

      val scanRequest:ScanRequest = new ScanRequest("test1")//.withScanFilter(scanFilter.asJava)
      //Scan read every item in table or secondary index.
      val sr:ScanResult = db.scan(scanRequest)
      info(s"${db.scan(scanRequest)}")
    }

    
    it("should have optional sorting by any field specified by query parameter, default sorting - by **id**;") (pending)
    it("should have functionality to return data for single car advert by id;")(pending)
    it("shoud have functionality to modify car advert by id;") (pending)
    it("shoud have functionality to delete car advert by id;") (pending)
  }

}

