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
import scala.util.{Either,Failure,Success,Try}

/**
 * Wrap some functionality of aws-sdk dynamodb client 
 * as database application.
 */
class Dynamodb @Inject()(conf:Configuration) extends Dba {
  type ContainerInfo = Unit

  // need fallback or fail strategy
  val ep  = conf.getString("aws.endpoint").get//.getOrElse("http://localhost:8000")
  val reg = conf.getString("aws.region").get  //.getOrElse("")
  val endpoint:EndpointConfiguration = new EndpointConfiguration(ep, reg)

  val b:AmazonDynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard
  implicit val db:AmazonDynamoDB = b.withEndpointConfiguration(endpoint).build

  def describe(name:String) = {
    val req:DescribeTableRequest = new DescribeTableRequest().withTableName(name)
    
    Option(db.describeTable(req)) match {
      case None => println(s"table $name doesnt exist")
      case Some(res:DescribeTableResult) => println(s"${res.getTable}")
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
    val throughput:ProvisionedThroughput =  new ProvisionedThroughput(1.toLong, 1.toLong)

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

}

// Simple attribute value wrapper
object Attribute {
  def apply(s:Any) = s match {
    case st:String  => new AttributeValue(st)
    case i:Int      => new AttributeValue().withN(i.toString)
    case b:Boolean  => new AttributeValue().withBOOL(b)
    case f:Fuel     => new AttributeValue(f.toString) // scala issue for type check
    case _          => new AttributeValue().withNULL(true)
  }
}
