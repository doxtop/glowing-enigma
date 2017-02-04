package adv

import org.scalameter.api._

import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB,AmazonDynamoDBClientBuilder}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.document._
import com.amazonaws.waiters._

import collection.JavaConverters._

import model._
/*
// [info] Obtained measurements:
// [info] item -> (): 9.939, 6.756, 7.360, 6.860, 8.309, 7.071, 6.904, 7.020, 15.140, 10.408, 8.788, 8.451, 9.378, 8.270, 6.721, 6.740, 13.004, 11.091, 7.874, 8.024, 8.818, 7.854, 7.367, 7.605, 7.770, 7.206, 8.525, 7.415, 11.256, 7.910, 8.750, 7.870, 12.335, 7.210, 6.880, 6.781
// [info] ::Benchmark dynamo client.putItem::
// [info] cores: 4
// [info] name: Java HotSpot(TM) 64-Bit Server VM
// [info] osArch: x86_64
// [info] osName: Mac OS X
// [info] vendor: Oracle Corporation
// [info] version: 25.112-b16
// [info] Parameters(put request time -> ()): 6.72106  

[info] Obtained measurements:
[info] scan time -> (): 35.598, 35.326, 34.248, 32.853, 44.764, 36.026, 35.340, 34.632, 32.086, 34.331, 36.145, 33.180, 39.760, 34.480, 44.088, 33.321, 40.323, 37.316, 31.269, 33.999, 37.046, 33.469, 33.085, 36.562, 41.532, 33.718, 33.528, 32.625, 31.931, 34.126, 35.953, 32.233, 33.464, 35.217, 32.999, 32.298
[info] ::Benchmark dynamo client.scan::
[info] cores: 4
[info] hostname: umb.tvintel.com.ua
[info] name: Java HotSpot(TM) 64-Bit Server VM
[info] osArch: x86_64
[info] osName: Mac OS X
[info] vendor: Oracle Corporation
[info] version: 25.112-b16
[info] Parameters(scan request time -> ()): 31.268901

*/
object RangeBenchmark extends Bench.LocalTime {
  val b:AmazonDynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard
  val endpoint:EndpointConfiguration = new EndpointConfiguration("http://localhost:8000", "")
  val db:AmazonDynamoDB = b.withEndpointConfiguration(endpoint).build

  import org.scalameter.picklers.Implicits._

  def uuid() = java.util.UUID.randomUUID.toString.replace("-","")

  performance of "dynamo client" in {
    measure method "putItem" in {
      using(Gen.unit("put request time")) in { case _ =>
        val it = Map(
          "id" -> new AttributeValue(uuid),
          "title" -> new AttributeValue("title"),
          "fuel" -> new AttributeValue(Diesel.toString),
          "price" -> new AttributeValue().withN("100"),
          "new" -> new AttributeValue().withBOOL(true),
          "mileage" -> new AttributeValue().withN("100"),
          "registration" -> new AttributeValue("01-01-1970"))

        db.putItem(new PutItemRequest("test1",it.asJava))
      }
    }

    measure method "scan" in {
      using(Gen.unit("scan request time")) in { case _ => 
        db.scan(new ScanRequest("test1"))
      }
    }
  }
}

