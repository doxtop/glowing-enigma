package adv

import play.api.{Environment, Configuration}
import com.google.inject.{AbstractModule,TypeLiteral}

import com.google.inject.name.Names

import store.{Dba,Dynamodb}
import service.{Api, Adverts}
import model.Car

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[Dba]).to(classOf[Dynamodb])
    bind(new TypeLiteral[Api[Car]]{}).annotatedWith(Names.named("car")).to(classOf[Adverts])
  }
}
