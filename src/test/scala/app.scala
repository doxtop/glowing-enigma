package adv

import play.api._
import play.api.ApplicationLoader.Context
import play.api.routing.sird._
import play.api.routing._
import play.api.test._

import controllers.{Adverts=>Ctl, Index}


import play.api.routing.Router._
import play.api.http._

import play.api.routing.Router


// router doesn't generated for compile time injecttion
// and failed with uncompatible methods in api.
// play.api.mvc.Results$class.$init$
class TestRouter() extends SimpleRouter {
  override def routes = {
    case GET(p"/") => new controllers.Index().index
  }
}

class TestAppLoader extends ApplicationLoader{
  override def load(context: ApplicationLoader.Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new AppComponents(context).application
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) {
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  lazy val dba:store.Dba = new store.Dynamodb(configuration)
  lazy val srv: service.Adverts = new service.Adverts(configuration, dba)
  lazy val applicationController = new controllers.Adverts(srv)
  lazy val index = new controllers.Index()
  lazy val assets = new controllers.Assets(httpErrorHandler)

  lazy val router:Router = new TestRouter
}
