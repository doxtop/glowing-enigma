package controllers

import play.api.mvc.{Action, Controller}
import javax.inject.Inject
import javax.inject.Named

import play.api.inject.guice._
import play.api.inject._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

//import com.google.inject._

import adv.service.Api
import adv._
import scalaz._, Scalaz._

import scala.concurrent.{ExecutionContext,Future}
/**
  * Service controller
  * Generator fro Action DSL to speak with service by the language of
  * service model understand(case classes)
  * here with speak jsons 
  *
  * its possible to speak json all the way through
  */
class HomeController @Inject()(@Named("car") service: Api[Car])(implicit ec: ExecutionContext) extends Controller {

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
  //implicit val fuelReads:Reads[Fuel] 

  implicit val carReads:Reads[Car] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "title").read[String] and
    ( Reads.pure(Diesel.asInstanceOf[Fuel]) ) and
    (JsPath \ "price").read[Int] and
    (JsPath \ "new").read[Boolean] and 
    (JsPath \ "mileage").readNullable[Int] and
    (JsPath \ "reg").readNullable[String]
  ) (Car.apply _ )

  def index = Action{r => Ok(html.index())}
  def echo  = Action{r => Ok(s"Got $r")}

  def delete(id: Int): Unit = ???
  def exist(id: Int): Unit = ???
  def get(id: Int): Unit = ???
  
  def post() = Action.async(parse.json) { implicit r =>
    //val data = Car("1002","Camaro SS", Gas, 100, true)
    //println(s"${r.body}")
    // from json
    val c:JsResult[Car] = Json.fromJson[Car](r.body)

    // include validation here
    println(s"Result: $c")
    // fold will work with validation
    c.map{ car =>
      println(s"parsed car: $car")
      service.post(car).map(r => Ok(Json.toJson(r)))
    }.getOrElse{
      println("real shit and bed request")
      BadRequest(Json.toJson(
        Map("status"->"shit")
      )).point[Future]
    }
  }

  def get = Action.async{ implicit r =>
    implicit val or:Order[Car] = Order.orderBy(_.id)

    service.get().map{ cars =>
      Ok(Json.toJson(cars))
    }

  } 

  def init(): Unit = ???
  def populate(): Unit = ???

}
