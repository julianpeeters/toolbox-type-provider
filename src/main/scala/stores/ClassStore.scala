package com.julianpeeters.toolbox.provider
package stores

import scala.collection.JavaConversions.JConcurrentMapWrapper
import java.util.concurrent. ConcurrentHashMap

object ClassStore {

  val generatedClasses: scala.collection.concurrent.Map[String, ToolBoxCaseClass] = JConcurrentMapWrapper(new ConcurrentHashMap[String, ToolBoxCaseClass]())

  def accept(tbcc: ToolBoxCaseClass) {
    if (!generatedClasses.contains(tbcc.fullName)) {
      generatedClasses += tbcc.name.toString -> tbcc
    }
  }
}
