package adv

import play.api.{Environment, Configuration}
import com.google.inject.{AbstractModule,TypeLiteral}

import com.google.inject.name.Names

import store.{Dba,Dynamodb}
import service.{Api, Adverts}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    // :define scopes later
    bind(classOf[Dba]).to(classOf[Dynamodb])
    bind(classOf[Kvs]).to(classOf[Store])
    bind(new TypeLiteral[Api[Car]]{}).annotatedWith(Names.named("car")).to(classOf[Adverts])
  }
}
