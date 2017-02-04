package adv

import play.api.{Environment, Configuration}
import com.google.inject.{AbstractModule,TypeLiteral}
import com.typesafe.config._

import store.{Dba,Dynamodb}
import service.{Api,Adverts}

import scala.concurrent._
import scala.concurrent.impl._

import com.google.inject.name.Names

/**
 * Scalatest related bindings. 
 */
class TestModule() extends AbstractModule{
  override def configure() = {
    bind(classOf[ExecutionContext]).toInstance(scala.concurrent.ExecutionContext.global)
    bind(classOf[Configuration]).toInstance(Configuration(ConfigFactory.load))
    bind(classOf[Dba]).to(classOf[Dynamodb])
    bind(classOf[Api[Car]]).annotatedWith(Names.named("car")).to(classOf[Adverts])
  }
}

/**
 * Test componets binding to use in Play application context
 * with own execution context and configuration.
 */
class PlayTestModule() extends AbstractModule{
  override def configure() = {
    bind(classOf[Dba]).to(classOf[Dynamodb])
    bind(new TypeLiteral[Api[Car]]{}).annotatedWith(Names.named("car")).to(classOf[Adverts])
  }
}