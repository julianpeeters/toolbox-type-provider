package com.julianpeeters.toolbox.provider

import models.FieldData
import scala.reflect.runtime.universe._

object Quasiquoter {

  //wraps a single field in a quasiquote, returning immutable field defs if immutable flag is true
  def quotifyField(field: FieldData, immutable: Boolean) = { 
    
    //"boxing" in this case is wrapping the string in [] so it looks correct for splicing
    def boxTypeTrees(typeName: String) = {
      val unboxedStrings = typeName.dropRight(typeName.count( c => c == ']')).split('[')
      val types = unboxedStrings.map(g => newTypeName(g)).toList  
      val typeTrees: List[Tree] = types.map(t => tq"$t")
      typeTrees.reduceRight((a, b) => tq"$a[$b]")
    }

    if (field.fieldType.endsWith("]")) { //if the field is a parameterized type
      val fieldTermName = newTermName(field.fieldName)
      val fieldTypeName = boxTypeTrees(field.fieldType)
      val defaultParam  = DefaultParamMatcher.asParameterizedDefaultParam(fieldTypeName.toString)
      if (immutable == false) q"""var $fieldTermName: $fieldTypeName = $defaultParam""" 
      else q"""val $fieldTermName: $fieldTypeName = $defaultParam"""
    }
    else { //if the field is a type that doesn't take type parameters
      val fieldTermName = newTermName(field.fieldName)
      val fieldTypeName = newTypeName(field.fieldType)
      val defaultParam  = DefaultParamMatcher.asDefaultParam(fieldTypeName.toString)
      if (immutable == false) q"""var $fieldTermName: $fieldTypeName = $defaultParam""" 
      else q"""val $fieldTermName: $fieldTypeName = $defaultParam"""
    }
  }
}
