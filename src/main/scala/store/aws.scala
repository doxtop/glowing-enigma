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
import scala.collection._
import scala.util.{Either,Failure,Success,Try}

/**
 * Wrap some functionality of aws-sdk dynamodb client 
 * as database application.
 * 
 */
class Dynamodb @Inject()(conf:Configuration) extends Dba {
  type ContainerInfo = Unit
  type Att = AttributeValue
  //override type Entry = immutable.Map[String, Att]

  // need fallback or fail strategy
  val ep  = conf.getString("aws.endpoint").get//.getOrElse("http://localhost:8000")
  val reg = conf.getString("aws.region").get  //.getOrElse("")
  val endpoint:EndpointConfiguration = new EndpointConfiguration(ep, reg)

  val b:AmazonDynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard
  implicit val db:AmazonDynamoDB = b.withEndpointConfiguration(endpoint).build

  /** Generate unique identifier to be used as entries primary key */
  def nextId():String = java.util.UUID.randomUUID.toString.replace("-","")

  /**
   * Retrieve container information.
   */
  def describe(name:String):String = {
    val req:DescribeTableRequest = new DescribeTableRequest().withTableName(name)
    
    Option(db.describeTable(req)) match {
      case None => s"table $name doesnt exist"
      case Some(res:DescribeTableResult) => s"${res.getTable}"
    }
  }

  /**
   * Create Table with name specified.
   * // configure
   */
  def createContainer(name:String):Res[ContainerInfo] = {
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
    
    Try{ db.createTable(request); w.run(wp) } match {
      case Success(_)   => Right(())
      case Failure(err) => Left(Dbe(msg = err.getMessage))
    }
  }

  /**
   * Delete table with name specified.
   */
  def deleteContainer(name:String):Res[ContainerInfo] = {
    val req:DeleteTableRequest = new DeleteTableRequest(name)
    val wp = new WaiterParameters(new DescribeTableRequest(name))
    val w = db.waiters().tableNotExists()
    
    Try{ db.deleteTable(req); w.run(wp) } match {
      case Success(_)   => Right(())
      case Failure(err) => Left(Dbe(msg=err.getMessage))
    }
  }

  /**
   *
   */
   def get(name:String, id:String):Res[Entry] = {
      Try(db.getItem(new GetItemRequest(name, Map("id"->Attribute(id)).asJava))) match {
        case Success(res:GetItemResult) =>
          Right(res.getItem.asScala.toMap)
        case Failure(err) => Left(Dbe(msg=err.getMessage))
      }
   }

  /**
   * 
   */
  def put(name:String, entry: Entry):Res[Entry] = {
    Try(db.putItem(new PutItemRequest(name, entry.asJava, ReturnValue.ALL_OLD))) match {
      case Success(res:PutItemResult) => 
        Right(Option(res.getAttributes).map(_.asScala.toMap).getOrElse(entry)) // respond: {}
      case Failure(err) => Left(Dbe(msg=err.getMessage))
    //ConditionalCheckFailedException
    //ProvisionedThroughputExceededException
    //ResourceNotFoundException
    //ItemCollectionSizeLimitExceededException
    //InternalServerErrorException
    }
  }

  /**
   *
   */
  def delete(name:String, id:String):Res[Entry] = {
    Try(db.deleteItem(new DeleteItemRequest(name, Map("id" -> Attribute(id)).asJava, ReturnValue.ALL_OLD))) match {
      case Success(res:DeleteItemResult) => 
        Right(res.getAttributes.asScala.toMap)
      case Failure(err) => Left(Dbe(msg=err.getMessage))
    }
  }

  def entries(name:String):Res[List[Entry]] = {
    val scanRequest:ScanRequest = new ScanRequest("test1").withConsistentRead(true)

    Try(db.scan(new ScanRequest(name).withConsistentRead(true))) match {
      case Success(sr:ScanResult) => 
        println(s"$sr")
        val last = Option(sr.getLastEvaluatedKey)
        Right(sr.getItems.asScala.map(_.asScala.toMap).toList)
      case Failure(err) => Left(Dbe(msg = err.getMessage))
    } 
  }

  //Create entry as map of atttibutes
  def entry(title:String,
    fuel:Fuel, 
    price:Int, 
    neu:Boolean, 
    mileage:Option[Int]=None, 
    reg:Option[String]=None) = immutable.Map[String,Att](
      "id" -> Attribute(nextId()),
      "title" -> Attribute(title),
      "fuel" -> Attribute(fuel),
      "price" -> Attribute(price),
      "new" -> Attribute(neu),
      "mileage" -> Attribute(mileage),
      "registration" -> Attribute(reg)
    ).filter(_._2.isNULL == null)

  // Simple attribute value wrapper
  object Attribute {
    def apply(s:Any):Att = s match {
      case st:String  => new AttributeValue(st)
      case i:Int      => new AttributeValue().withN(i.toString)
      case b:Boolean  => new AttributeValue().withBOOL(b)
      case f:Fuel     => new AttributeValue(f.toString)
      case Some(so:String)=> new AttributeValue(so)
      case Some(io:Int)   => new AttributeValue().withN(io.toString)
      case _              => new AttributeValue().withNULL(true)
    }
  }
}
