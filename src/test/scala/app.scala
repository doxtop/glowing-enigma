package adv

import play.api._
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import router.Routes

import controllers.{Adverts=>Ctl, Index, Assets}

/**
 * Load application for test scope without any DI.
 */
class TestAppLoader extends ApplicationLoader{
  override def load(context: ApplicationLoader.Context): Application = {
    new AppComponents(context).application
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  lazy val dba:store.Dba = new store.Dynamodb(configuration)
  lazy val srv:service.Adverts = new service.Adverts(configuration, dba)
  lazy val adverts:Ctl = new Ctl(srv)
  lazy val index:Index = new Index()
  lazy val assets:Assets = new Assets(httpErrorHandler)

  lazy val router:Router = new Routes(httpErrorHandler, index, adverts, assets)
}
