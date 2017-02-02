package adv

import play.api.{Environment, Configuration}
import com.google.inject.AbstractModule

import store.{Dba,Dynamodb}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[Dba]).to(classOf[Dynamodb])
    bind(classOf[Kvs]).to(classOf[Store])
  }
}
