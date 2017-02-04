package adv
package service

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object CarAdvertsFormat{

  implicit val carWrites = new Writes[Car]{
    def writes(c:Car):JsValue = {
      Json.obj(
        "id"  -> c.id,
        "title" -> c.title,
        "fuel" -> c.fuel.toString,
        "price" -> c.price,
        "new" -> c.neu,
        "mileage" -> c.mileage,
        "reg" -> c.reg
        )
    }
  }
  //(unlift(Document.unapply))

  implicit val fuelReads:Reads[Fuel] = new Reads[Fuel]{
    def reads(js:JsValue) = 
      JsSuccess(Diesel)
  }
 
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
