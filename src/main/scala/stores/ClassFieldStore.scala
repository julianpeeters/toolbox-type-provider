package com.julianpeeters.toolbox.provider
package stores

import models.{ ClassData, FieldData }

import java.util.concurrent.ConcurrentHashMap
import collection.JavaConversions._

object ClassFieldStore {

  val fields: scala.collection.concurrent.Map[String, List[FieldData]] = scala.collection.convert.Wrappers.JConcurrentMapWrapper(new ConcurrentHashMap[String, List[FieldData]]())

  // add it to the store with it's name as the key
  def storeClassFields(classData: ClassData): Unit = {
    ClassFieldStore.fields += (classData.name.toString -> classData.fields)
  }
}
