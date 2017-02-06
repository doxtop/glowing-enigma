package adv
package service


import scala.concurrent.Future
import scalaz._,Scalaz._
import store.{Dba,Handler}

/**
 * Feed REST Service Api.
 */
trait Api[T] {

  def init():Res[String]
  def get()(implicit o:Order[T]):Future[List[T]]
  def put(e:T)        :Future[Res[T]]
  def get(id:String)  :Future[Res[T]]
  def del(id:String)  :Future[Res[T]]
  def update(e:T)     :Future[Res[T]]

}
