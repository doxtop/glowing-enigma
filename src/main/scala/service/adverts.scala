package adv
package service

import javax.inject._
import play.api.Configuration

import scala.concurrent._

import store.{Dba,Handler,CarAdvertsSchema}
import model.Car

/**
 *  Car adverts REST service implementation.
 *  
 *  Adds sorting functionality to DBA handlers which is not support sorting.
 */
class Adverts @Inject()(conf:Configuration, dba:Dba)(implicit ec:ExecutionContext) extends Api[Car] {
  import scalaz._, Scalaz._  

  // more  table/index selection here
  val table = "adverts"

  // init here for the moment
  init()

  def init():Res[String] = {
    // need handler for container types.
    dba.describe(table) ||| dba.createContainer(table)
  }

  // check dba configs, prepare tables, create names.
  import CarAdvertsSchema._
  implicit val hand:Handler[Car] = implicitly[Handler[Car]]

  def get()(implicit o: Order[Car]): Future[List[Car]] = {
    val x:Err\/List[Car] = hand.entries(table)(dba)
    val ord = o.toScalaOrdering
    
    (x.map( _.sorted(ord)) | List.empty[Car]).point[Future]
  }

  def put(e:Car)      : Future[Res[Car]] = hand.put(table, e)   (dba).point[Future]
  def get(id: String) : Future[Res[Car]] = hand.get(table, id)  (dba).point[Future]
  def del(id: String) : Future[Res[Car]] = hand.del(table,id)(dba).point[Future]
  def update(e: Car)  : Future[Res[Car]] = hand.update(table,e) (dba).point[Future]
}
