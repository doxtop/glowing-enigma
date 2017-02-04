package adv
package model

import scala.language.implicitConversions

import scalaz._,Scalaz._

// Data used in service as entry/item (store/dba) analogue.
case class Car(
  id:String, 
  title:String,
  fuel:Fuel,
  price:Int,
  neu:Boolean = true,
  mileage:Option[Int] = None,
  reg:Option[String] = None)

object CarOps{
  import FuelOps._

  //def cmFn: Car => String = (car) => car.id
  // |+| - Order semigroup can be combined
  implicit def orderCar(s:String):Order[Car] = s match {
    case "id"     => Order.orderBy((_:Car).id)
    case "title"  => Order.orderBy((_:Car).title)
    case "fuel"   => Order.orderBy((_:Car).fuel)
    case "price"  => Order.orderBy((_:Car).price)
    case "new"    => Order.orderBy((_:Car).neu)
    case "mileage"=> Order.orderBy((_:Car).mileage)
    case "reg"    => Order.orderBy((_:Car).reg)
    case _        => Order.orderBy((_:Car).id)
  }

}
