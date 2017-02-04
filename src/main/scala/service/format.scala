package adv
package service

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import model._

object CarAdvertsFormat{
  import CarOps._
  import FuelOps._

  // read 
  implicit val fuelReads:Reads[Fuel] = new Reads[Fuel]{
    def reads(js:JsValue) = js match {
      //case JsString(f) => implicitly[Read[Fuel]].read(f)
      case _ => JsSuccess(Diesel)
    }
  }
  
  implicit val fuelWrites = Writes[Fuel](fl => JsString(fl.toString))// shows must work

  implicit val carWrites:Writes[Car] = (
    (JsPath  \ "id").write[String] and 
    (JsPath  \ "title").write[String] and
    (JsPath  \ "fuel").write[Fuel] and
    (JsPath  \ "price").write[Int] and
    (JsPath  \ "new").write[Boolean] and 
    (JsPath  \ "mileage").writeNullable[Int] and
    (JsPath  \ "reg").writeNullable[String]
  ) (unlift(Car.unapply))

  implicit val carReads:Reads[Car] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "title").read[String] and
    (Reads.pure(Diesel.asInstanceOf[Fuel]) ) and // need Fuel
    (JsPath \ "price").read[Int] and
    (JsPath \ "new").read[Boolean] and 
    (JsPath \ "mileage").readNullable[Int] and
    (JsPath \ "reg").readNullable[String]
  ) (Car.apply _ )
  
}
