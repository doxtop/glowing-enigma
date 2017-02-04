package adv
package store

trait Handler[T]{
  def pickle(e:T):Entry
  def put(fid:String,t:T)(implicit dba:Dba):Res[T]
  def entries(fid:String)(implicit dba:Dba):Res[List[T]]
}

object Handler{
  def apply[T](implicit h:Handler[T]) = h
}
