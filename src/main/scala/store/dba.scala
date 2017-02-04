package adv
package store

import scala.collection._

/**
 * Database application API.
 *
 * DBA provides the operation with collection of data:
 * 
 * Container - collection of entries
 * Entry     - collection of attributes
 * Attribute - (key,value)
 *
 * Check supported types in package object.
 */
trait Dba {
  type Container

  def nextId():String
  def describe(name:String):String
  def createContainer(name:String):Res[Container]
  def deleteContainer(name:String):Res[Container]

  def get(name:String, id:String)   :Res[Entry]
  def put(name:String, entry:Entry) :Res[Entry]
  def del(name:String, id:String)   :Res[Entry]

  def entries(name:String):Res[List[Entry]]  
}
