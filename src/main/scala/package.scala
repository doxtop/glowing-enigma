/**
 * Defines application types here for now.
 */
import scalaz._, Scalaz._

package object adv {

  // // application model types
  // import model.FuelOps.order
  
  // storage types
  sealed trait Err
  case object NotImplemented extends Err
  case class Dbe(name:String = "error", msg:String) extends Err
  case class NotFound(msg:String) extends Err
  case class NotGranted(msg:String) extends Err
  case class Timeout(msg:String) extends Err

  type Res[T] = Err \/ T


  // breaking law 'monad' for Future
  import scala.concurrent._
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val futureMonad = new Monad[Future] {
    override def point[A](a: ⇒ A): Future[A] = Future(a)
    override def bind[A, B](fa: Future[A])(f: A ⇒ Future[B]): Future[B] = fa.flatMap(f)
  }

}
