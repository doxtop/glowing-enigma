package adv
package store

/**
 * Database application API.
 *
 * DBA provides the operation with collection of data:
 * 
 * Container - collection of entries
 * Entry     - collection of attributes
 * Attribute - (key,value)
 */
trait Dba {
  type ContainerInfo 
  
  def describe(name:String)
  def createContainer(name:String):Res[ContainerInfo]
  def deleteContainer(name:String):Res[ContainerInfo]
}
