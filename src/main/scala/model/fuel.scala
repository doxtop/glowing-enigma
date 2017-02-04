package adv
package model

import scalaz._,Scalaz._

// Car fuel definition

sealed trait Fuel
case object Gas extends Fuel
case object Diesel extends Fuel

trait Read[A] { def read(a: String): Option[A] }

object FuelOps {
  implicit val order:Order[Fuel] = new Order[Fuel]{
    def order(a:Fuel, b:Fuel):Ordering = (a,b) match {
      case (Diesel,Diesel)  => Ordering.EQ
      case (Gas, Gas)       => Ordering.EQ
      case (_, Diesel)      => Ordering.GT
      case (Diesel, _)      => Ordering.LT
    }
  }

  implicit val show:Show[Fuel] = new Show[Fuel] {
    override def shows(f:Fuel):String = f.toString
  }
  
  implicit val read: Read[Fuel] = new Read[Fuel] {
    def read(f: String): Option[Fuel] =
      f match {
        case "Gas"    => Some(Gas)
        case "Diesel" => Some(Diesel)
        case _        => None
      }
  } 
}
