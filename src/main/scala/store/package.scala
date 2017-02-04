package adv 

import store._
import scala.collection.immutable.Map

import com.amazonaws.services.dynamodbv2.model.AttributeValue

package object store {
  //type Attribute = String
  type Entry = Map[String,Any]
}
