package adv 

import store._
import model._
import scala.collection.immutable.Map

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import com.amazonaws.waiters.WaiterTimedOutException
import scalaz._, Scalaz._

/** 
 * Database application layer(package) types.
 */
package object store {

  // entry attributed must be supported by dba implementation
  sealed trait Att
  case class St(s:String) extends Att
  case class Bl(b:Boolean) extends Att
  case class In(i:Int) extends Att
  case class Fl(f:Fuel) extends Att
  case class So(s:Option[String]) extends Att
  case class Io(i:Option[Int]) extends Att
  case class Nl() extends Att

  // dba implementation must treat the entry as map.
  type Container = String
  type Entry = Map[String,Att]

  // convert from try to storage result
  import scala.util.control.NonFatal

  def toRes[T](a: => T): Err \/ T = try {
    \/-(a)
  } catch { 
    case n:ResourceNotFoundException  => -\/(NotFound(n.getErrorMessage))
    case w:WaiterTimedOutException    => -\/(Timeout(w.getMessage))
    //case w:WaiterUnrecoverableException =>
    //case a:AmazonDynamoDBException => 
    //case a:AmazonServiceException =>
    case NonFatal(t) => -\/(Dbe(msg=t.getMessage)) 
  }

}
