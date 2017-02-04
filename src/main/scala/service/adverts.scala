package adv
package service

import javax.inject._
import play.api.Configuration

import scala.concurrent._

import store.{Dba,Handler}

/*

  Error handled on that level - simple dba errors
  should they be populated to http

*/
class Adverts @Inject()(conf:Configuration, dba:Dba)(implicit ec:ExecutionContext) extends Api[Car] {
  import scalaz._, Scalaz._  

  import CarAdvertsSchema._

  val table = "test1"

  def init(): Unit = {
    println(s"Init service. Check dba tables etc.")  
  }

  def delete(id: Int): Unit = ???
  def exist(id: Int): Unit = ???
  def get(id: Int): Unit = ???

  def populate(): Unit = ???

  def post(e:Car):Future[Res[Car]] = implicitly[Handler[Car]].put(table, e)(dba).point[Future]

  def get()(implicit o: Order[Car]): Future[List[Car]] = {
    println(s"Get the car adverts list by $o")

    val x:Err\/List[Car] = implicitly[Handler[Car]].entries(table)(dba)

    val ord = o.toScalaOrdering
    
    (x.map( _.sorted(ord)) | List.empty[Car]).point[Future]
  }

}
