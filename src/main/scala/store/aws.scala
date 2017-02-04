package adv
package store

import javax.inject._
import play.api.Configuration

import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB,AmazonDynamoDBClientBuilder}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.document._
import com.amazonaws.waiters._

import collection.JavaConverters._
import scala.collection.immutable.Map
import scala.util.{Either,Failure,Success,Try}

import scala.language.implicitConversions


/*
 Dynamodb atttibute values pickler.

*/
class AwsAttributes(a:Att, b:AttributeValue){
  import scalaz._, Scalaz._
  def pickle():AttributeValue = {
    a match {
      case St(st) => new AttributeValue(st)
      case Bl(b)  => new AttributeValue().withBOOL(b)
      case In(i)  => new AttributeValue().withN(i.toString)
      case Fl(f)  => new AttributeValue(f.toString)
      case So(Some(so)) => new AttributeValue(so)
      case Io(Some(io)) => new AttributeValue().withN(io.toString)
      case _ => new AttributeValue().withNULL(true)
    }
  }
  def unpickle():Att = {
    //The same mess everywehre :(
    if      (b.isNULL != null) Nl()
    else if (b.isBOOL != null) Bl(b.getBOOL)
    // just fast non typed stuff ... maybe validation, \/, reader
    else if (b.toString.startsWith("{S: Gas")) Fl(Gas)
    else if (b.toString.startsWith("{S: Diesel")) Fl(Diesel)
    else if (b.toString.startsWith("{S:")) St(b.getS)
    else if (b.toString.startsWith("{N:")) b.getN.parseInt.leftMap(_=> 0).fold(In(_),In(_))
    else if (b.toString.startsWith("{BOOL:")) Bl(b.getBOOL)
    else if (b.toString.startsWith("{NULL:")) Nl()
    else Nl()
  }
}

/**
 * Wrap some functionality of aws-sdk dynamodb client as database application.

 */
class Dynamodb @Inject()(conf:Configuration) extends Dba {
  import scalaz._, Scalaz._

  type Key = java.util.Map[String,AttributeValue]
  type Item = Map[String, AttributeValue]

  type Container = Unit

  // need fallback or fail strategy
  val ep  = conf.getString("aws.endpoint").get//.getOrElse("http://localhost:8000")
  val reg = conf.getString("aws.region").get  //.getOrElse("")
  val endpoint:EndpointConfiguration = new EndpointConfiguration(ep, reg)

  val b:AmazonDynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard
  implicit val db:AmazonDynamoDB = b.withEndpointConfiguration(endpoint).build

  // inject dynamodb style attribute values
  implicit def attPickle(a:Att) = new AwsAttributes(a, new AttributeValue().withNULL(true))
  implicit def attUnpickle(b:AttributeValue) = new AwsAttributes(Nl(), b)

  /** Generate unique identifier to be used as entries primary key */
  def nextId():String = java.util.UUID.randomUUID.toString.replace("-","")

  /**
   * Retrieve container information.
   */
  def describe(name:String):String = {
    val req:DescribeTableRequest = new DescribeTableRequest().withTableName(name)
    
    Option(db.describeTable(req)) match {
      case None => s"table $name doesn't exist"
      case Some(res:DescribeTableResult) => s"${res.getTable}"
    }
  }

  /**
   * Create Table with name specified.
   * // configure
   */
  def createContainer(name:String):Res[Container] = {
    val attDef:Seq[AttributeDefinition] = Seq(
      new AttributeDefinition("id", ScalarAttributeType.S)
    )
    val keySchema:Seq[KeySchemaElement] = Seq(
      new KeySchemaElement("id", KeyType.HASH)
    )
    val throughput:ProvisionedThroughput =  new ProvisionedThroughput(10.toLong, 10.toLong)

    val request:CreateTableRequest = new CreateTableRequest()
      .withAttributeDefinitions(attDef.asJava)
      .withKeySchema(keySchema.asJava)
      .withProvisionedThroughput(throughput)
      .withTableName(name)

    val wp = new WaiterParameters(new DescribeTableRequest(name))
    val w = db.waiters().tableExists
    
    toRes {db.createTable(request); w.run(wp)}
  }

  /**
   * Delete table with name specified.
   */
  def deleteContainer(name:String):Res[Container] = {
    val req:DeleteTableRequest = new DeleteTableRequest(name)
    val wp = new WaiterParameters(new DescribeTableRequest(name))
    val w = db.waiters().tableNotExists()
    
    toRes {db.deleteTable(req); w.run(wp)} 
  }

  /**
   */
   def get(name:String, id:String):Res[Entry] = {
      var req = new GetItemRequest(name, Map("id"-> St(id).pickle).asJava)

      toRes(db.getItem(req))
        .map(_.getItem.asScala.toMap)
        .map(_.map{case (k,v) => k->v.unpickle}.filter(_._2 != Nl()))
   }

  /**
   */
  def put(name:String, entry: Entry):Res[Entry] = {
    val m1:Map[String, AttributeValue] = entry.map{case (k,v) => (k, v.pickle)}

    val req = new PutItemRequest(name, m1.asJava, ReturnValue.ALL_OLD)

    toRes(db.putItem(req))
      .map(r => Option(r.getAttributes).map(_.asScala.toMap)
      .map(_.map{case (k,v) => k-> v.unpickle}.filter(_._2 != Nl()))
      .getOrElse(entry))
  }

  /**
   */
  def del(name:String, id:String):Res[Entry] = {
    var req = new DeleteItemRequest(name, Map("id" -> St(id).pickle).asJava, ReturnValue.ALL_OLD)

    toRes(db.deleteItem(req)).map(_.getAttributes.asScala.toMap)
      .map(_.map{case (k,v) => k -> v.unpickle}.filter(_._2 != Nl()))
  }

  /**
   */
  def entries(name:String):Res[List[Entry]] = {
    val req:ScanRequest = new ScanRequest(name).withLimit(10)

    def scan(req:ScanRequest, acc:List[Item], last:Option[Key]):Res[List[Item]] = {
      val req1 = req.withExclusiveStartKey(last.getOrElse(null))

      toRes (db.scan(req1)).flatMap { s1 =>

        val itm:List[Item] = s1.getItems.asScala.map(_.asScala.toMap).toList
        val list:List[Item] = acc ::: itm

        Option(s1.getLastEvaluatedKey) match {
          case None => \/-(list)
          case key  => scan(req, list, key)
        }
      }
    }

    // Res[List[Item]] -> Res[List[Entry]]
    scan(req, List.empty, None)
      .map(_.map(_.map{case (k,v) => k-> v.unpickle}.filter(_._2 != Nl())))
  }
}
