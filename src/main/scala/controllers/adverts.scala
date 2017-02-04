package controllers

import play.api.mvc.{Action,Controller,BodyParsers, Result}
import javax.inject.Inject
import javax.inject.Named

import play.api.libs.json._
import play.api.mvc.Results.{Ok,NotFound, InternalServerError => IErr}

import adv.service.{Api,CarAdvertsFormat}
import scalaz._, Scalaz._
import adv._, model._

import scala.concurrent.{ExecutionContext,Future}

/**
  * Service controller to serve car adverts.
  * Generator for Action DSL
  * 
  * json <-> Car
  */
class Adverts @Inject()(@Named("car") service: Api[Car])(implicit ec: ExecutionContext) extends Controller {

  import CarAdvertsFormat._
  // configure timeouts

  def validateJson[A:Reads] = BodyParsers.parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def toRes(res:Res[Car]):Result = res.fold(l=>NotFound(Json.toJson(l.toString)) ,r=> Ok(Json.toJson(r)))

  // post an advert
  def post() = Action.async(validateJson[Car]) { implicit r =>
    service.put(r.body)
      //.map(toRes)
      .map(_.fold(l=> IErr(Json.toJson(l.toString)), r => Ok(Json.toJson(r))))
  }

  // list all available adverts sorted and always ok (maybe add error)
  def list(sort:Option[String], ord:Option[String]) = Action.async{ implicit r =>
    //second parameter is always None for som reason
    val notworking: Option[String] = r.getQueryString("ord")

    import CarOps._
    
    val or:Order[Car] = sort.map(orderCar).getOrElse(Order.orderBy((_:Car).id))

    // |+| - Order semigroup can be combined

    service.get()(or)
      .map{ cars => Ok(Json.toJson(cars))}
  } 

  // read the advert by id
  def get(id: String) = Action.async( service.get(id) .map(toRes) )
  
  // remove the advert by id
  def del(id: String) = Action.async( service.del(id).map(toRes) )

  // update the adver by id 
  def put(id: String) = Action.async(parse.json) {implicit r => 
    Json.fromJson[Car](r.body).fold(
      invalid = { err =>
        BadRequest(Json.toJson(err.head.toString)).point[Future]
      },
      valid = { car => 
        //update the car here!
        service.put(car).map(toRes)
      })
  }
}
