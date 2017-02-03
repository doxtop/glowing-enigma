package controllers

import play.api.mvc.{Action, Controller}
import javax.inject.Inject
import javax.inject.Named

import play.api.inject.guice._
import play.api.inject._
import play.api.libs.json._
//import com.google.inject._

import adv.service.Api
import adv._
import scalaz._, Scalaz._

import scala.concurrent.{ExecutionContext,Future}
/**
  * Service controller
  */
class HomeController @Inject()(@Named("car") service: Api[Car])(implicit ec: ExecutionContext) extends Controller {

  def index = Action{r => Ok(html.index())}
  def echo  = Action{r => Ok(s"Got $r")}

  def delete(id: Int): Unit = ???
  def exist(id: Int): Unit = ???
  def get(id: Int): Unit = ???
  
  def get = Action.async{ implicit r =>
    implicit val or:Order[Car] = Order.orderBy(_.id)

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

    service.get().map{ cars =>
      Ok(Json.toJson(cars))
    }

  } 

  def init(): Unit = ???
  def populate(): Unit = ???
  def post(js: String): Unit = ???
  def post(a: (Int, Int)): Unit = ???

}
