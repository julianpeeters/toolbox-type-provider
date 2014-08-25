package com.julianpeeters.toolbox.provider
package models

case class ClassData(
  packageName: Option[String], 
  name: String, 
  fields: List[FieldData])


