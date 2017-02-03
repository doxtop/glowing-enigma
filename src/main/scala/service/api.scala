package adv
package service

import scalaz._, Scalaz._
import scala.concurrent.Future

/**
  Non-blocking REST Service Api.

  resourse exist
  methods allowed POST,GET,PUT,DELETE
  content type provideded/accepted
  handle json,urlencode,formdata  
 */
trait Api[T] {
  // bootstrap service
  def init()
  def populate()
  def exist(id:Int) // short

  // return all with defined sorting order
  def get()(implicit o:Order[T]):Future[List[T]]

  def get(id:Int)
  def delete(id:Int) 
  def post(a:(Int,Int))
  def post(js:String)
}
