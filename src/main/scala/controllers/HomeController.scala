package controllers

import play.api.mvc.{Action,Controller,BodyParsers}
import javax.inject.Inject
import javax.inject.Named

import play.api.libs.json._
import play.api.mvc.Results.{Ok,NotFound, InternalServerError => IErr}

import adv.service.{Api,CarAdvertsFormat}
import adv.Car
import scalaz._, Scalaz._

import scala.concurrent.{ExecutionContext,Future}

/**
  * Service controller
  * Generator for Action DSL
  * 
  * json <-> Car
  */
class HomeController @Inject()(@Named("car") service: Api[Car])(implicit ec: ExecutionContext) extends Controller {

  import CarAdvertsFormat._
  // configure timeouts

  def index = Action{r => Ok(html.index())}

  def validateJson[A:Reads] = BodyParsers.parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  // post an advert
  def post() = Action.async(validateJson[Car]) { implicit r =>
    service.put(r.body)
      .map(_.fold(l=> IErr(Json.toJson(l.toString)), r => Ok(Json.toJson(r))))
  }

  // list all available adverts sorted and always ok (maybe add error)
  def list = Action.async{ implicit r =>
    implicit val or:Order[Car] = Order.orderBy(_.id)

    service.get()
      .map{ cars => Ok(Json.toJson(cars))}
  } 

  // read the advert by id
  def get(id: String) = Action.async {implicit r => 
    service.get(id)
      .map(_.fold(l=>NotFound(Json.toJson(l.toString)) ,r=> Ok(Json.toJson(r))))
  }

  // remove the advert by id
  def del(id: String) = Action.async{ implicit r =>
    service.del(id)
      .map(_.fold(l=> NotFound(l.toString), c => Ok(Json.toJson(c))))
  }

  // update the adver by id 
  def put(id: String) = Action.async(parse.json) {implicit r => 
    Json.fromJson[Car](r.body).fold(
      invalid = { err =>
        BadRequest(Json.toJson(err.head.toString)).point[Future]
      },
      valid = { car => 
        //update the car actually 
        service.put(car)
          .map(_.fold(l=> NotFound(Json.toJson(l.toString)), r=> Ok(Json.toJson(r))))
      })
  }
}
