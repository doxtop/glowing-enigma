/**
 * Defines application types here for now.
 */
import scalaz._
import syntax.either._

package object adv {

  // application model types
  sealed trait Fuel
  case object Gas extends Fuel
  case object Diesel extends Fuel

  case class Car(
    id:String, 
    title:String,
    fuel:Fuel,
    price:Int,
    neu:Boolean = true,
    mileage:Option[Int] = None,
    reg:Option[String] = None)

  // storage types
  sealed trait Err
  case class Dbe(name:String = "error", msg:String) extends Err
  type Res[T] = Err \/ T
}
