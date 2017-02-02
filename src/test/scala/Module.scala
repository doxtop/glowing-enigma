package adv

import play.api.{Environment, Configuration}
import com.google.inject.AbstractModule
import com.typesafe.config._

import store.{Dba,Dynamodb}

class TestModule() extends AbstractModule{
  override def configure() = {
    bind(classOf[Dba]).to(classOf[Dynamodb])
    bind(classOf[Configuration]).toInstance(Configuration(ConfigFactory.load))
  }
}
