package adv
package servoce

import scala.collection.immutable.Map
import scalaz._,Scalaz._

import model._
import store._

/*
 * Schema is a set of entry markers and specific handlers.
 */
object CarAdvertsSchema {

  // here is the complete mess with types and attributes

  implicit object carAdvHandler extends Handler[Car]{
    def as[T](a:Att):T = a match {
      case St(a) => a.asInstanceOf[T]
      case Fl(f) => f.asInstanceOf[T]      
      case In(i) => i.asInstanceOf[T]
      case Bl(b) => b.asInstanceOf[T]
      case Io(None) => 0.asInstanceOf[T]
      case Io(Some(i)) => i.asInstanceOf[T]
      case So(None)    => "".asInstanceOf[T]
      case So(Some(s)) => s.asInstanceOf[T]
      case s => s.asInstanceOf[T]
    }

    def pickle(c:Car):Entry = Map(
      "id"      -> St(c.id),
      "title"   -> St(c.title),
      "fuel"    -> Fl(c.fuel),
      "price"   -> In(c.price),
      "new"     -> Bl(c.neu),
      "mileage" -> Io(c.mileage),
      "reg"     -> So(c.reg))

    def unpickle(e:Entry):Car = (  
            e.get("id").map(as[String](_))
        |@| e.get("title").map(as[String](_)) 
        |@| e.get("fuel").map(as[Fuel](_))
        |@| e.get("price").map(as[Int](_))
        |@| e.get("new").map(as[Boolean](_)) 
        |@| e.get("mileage")
              .map(as[Int](_))
              .map(_.toInt).point[Option]
        |@| e.get("reg").map(as[String](_)).point[Option]) (Car.apply).get

    def entries(fid:String)(implicit dba:Dba):Res[List[Car]] =
      dba.entries(fid)
        .map(list => list.map(e=> unpickle(e)))

    /**
     * Let the dba level generate the identifier for new entry.
     */
    def put(fid:String,c:Car)(implicit dba:Dba):Res[Car] = {
        val eid = dba.nextId()
        dba.put(fid, pickle(c.copy(id=eid))).map(unpickle)
    }
    def get(fid:String,id:String)(implicit dba:Dba):Res[Car] = dba.get(fid, id).map(unpickle)
    def del(fid:String,id:String)(implicit dba:Dba):Res[Car] = dba.del(fid, id).map(unpickle)
    def update(fid:String,c:Car)(implicit dba:Dba):Res[Car]  = dba.put(fid, pickle(c)).map(unpickle)
  }
}
