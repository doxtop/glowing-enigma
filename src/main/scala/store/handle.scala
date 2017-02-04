package adv
package store

trait Handler[T]{
  def pickle(e:T):Entry
  def unpickle(e:Entry):T
  def put(fid:String,t:T)(implicit dba:Dba):Res[T]
  def get(fid:String,t:String)(implicit dba:Dba):Res[T]
  def del(fid:String,t:String)(implicit dba:Dba):Res[T]
  def update(fid:String,t:T)(implicit dba:Dba):Res[T]
  def entries(fid:String)(implicit dba:Dba):Res[List[T]]
}

object Handler{
  def apply[T](implicit h:Handler[T]) = h
}
