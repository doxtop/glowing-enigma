package adv
package service

import store._

import scala.collection.immutable.Map
import scalaz._,Scalaz._
/*
* Schema is a set of entry markers and specific handlers.
*/
object CarAdvertsSchema {

  implicit object carAdvHandler extends Handler[Car]{

    def pickle(c:Car):Entry = Map(
      "id"  -> c.id,
      "title" -> c.title,
      "fuel" -> c.fuel.toString,
      "price" -> c.price.toString,
      "new" -> c.neu.toString,
      "mileage" -> c.mileage.shows,
      "reg" -> c.reg.shows)

    def unpickle(e:Entry):Car = {
      (e.get("id").map(_.toString)
        |@| e.get("title").map(_.toString) 
        |@| e.get("fuel").map(_ => Diesel)
        |@| e.get("price").map(_.toString.toInt)
        |@| e.get("new").map(_.toString.toBoolean) 
        |@| e.get("mileage").map(_.toString.parseInt.toOption)
        |@| e.get("reg").map(_.toString.point[Option]) ) (Car.apply) .get
    }

    def entries(fid:String)(implicit dba:Dba):Res[List[Car]] = {
      val enList:Res[List[Entry]] = dba.entries(fid)

      enList.map(list => list.map(e=> unpickle(e)))
    }

    def put(fid:String,c:Car)(implicit dba:Dba):Res[Car] = {
      val e:Entry = pickle(c)

      val de = dba.entry(e)
      // conver all fields to attributes here
      val e1 = dba.put(fid, de)

      e1.map(unpickle)
    }
  }
  
}
