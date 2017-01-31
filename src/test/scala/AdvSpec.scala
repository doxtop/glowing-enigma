package adv

import org.scalatest._
//import org.scalacheck._
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB,AmazonDynamoDBClientBuilder}

class AdvSpec extends FlatSpecLike {
  behavior of "Adv Servce"

  //not the actual test just help to run in dev mode
  it should "help to run" in {
    info(s"hello, sailor")
  }

  it should "create dynamo table" in {
    import com.amazonaws.AmazonServiceException
    import com.amazonaws.services.dynamodbv2.model.{AttributeDefinition, 
      CreateTableRequest, CreateTableResult, 
      KeySchemaElement, KeyType, ProvisionedThroughput, ScalarAttributeType}

    val table_name:String = "demoTable1"

    val request:CreateTableRequest = new CreateTableRequest()
      .withAttributeDefinitions(new AttributeDefinition("Name", ScalarAttributeType.S))
      .withKeySchema(new KeySchemaElement("Name", KeyType.HASH))
      .withProvisionedThroughput(new ProvisionedThroughput(10.toLong, 10.toLong))
      .withTableName(table_name);

    import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
    
    val b:AmazonDynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard
    val endpoint:EndpointConfiguration = new EndpointConfiguration("http://localhost:8000", "")
    val db:AmazonDynamoDB = b.withEndpointConfiguration(endpoint).build

    try {
      val result:CreateTableResult = db.createTable(request)
    } catch {
      case e:AmazonServiceException => println(e.getErrorMessage())
    }

    db.shutdown
  }
}
