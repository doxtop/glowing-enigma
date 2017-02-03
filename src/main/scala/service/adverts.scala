package adv
package service

import javax.inject._
import play.api.Configuration

import scala.concurrent._

class Adverts @Inject()(conf:Configuration)(implicit ec:ExecutionContext) extends Api[Car] {
  import scalaz._, Scalaz._  

  def init(): Unit = {
    println(s"Init service. Check dba tables etc.")  
  }

  def delete(id: Int): Unit = ???
  def exist(id: Int): Unit = ???
  def get(id: Int): Unit = ???

  def populate(): Unit = ???
  def post(js: String): Unit = ???
  def post(a: (Int, Int)): Unit = ???

  def get()(implicit o: Order[Car]): Future[List[Car]] = {
    println(s"Get the car adverts list by $o")
    List.empty[Car].point[Future]
  }

}
