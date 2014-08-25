package com.julianpeeters.toolbox.provider

import models.FieldData
import stores.ClassFieldStore
import scala.reflect.runtime.universe._

object DefaultParamMatcher {

  def getApplyParams(typeName: String): List[Tree] = {

    def asApplyParam(field: FieldData) = {
      if (field.fieldType.endsWith("]")) DefaultParamMatcher.asParameterizedDefaultParam(field.fieldType)
      else  DefaultParamMatcher.asDefaultParam(field.fieldType)
    }

    /*
    * Uses the typename (here a class name) as a key to get a list of its fields,
    * map each field to a its apply parameter, giving a list of q"Literals".
    */
    if (ClassFieldStore.fields.get(typeName).isDefined) {
      ClassFieldStore.fields.get(typeName).get.map(field => asApplyParam(field))
    }
    else error("uh oh, didn't find a class corresponding to that type name in the ClassFieldStore: " + typeName)
  }

  def asDefaultParam(fieldTypeName: String) = {
    fieldTypeName match {
      case "Unit"    => q"()"
      case "Boolean" => q""" true """
      case "Int"     => q"1"
      case "Long"    => q"1L"
      case "Float"   => q"1F"
      case "Double"  => q"1D"
      case "String"  => q""" "s" """
      case "Null"    => q"null"
      case "Char"    => q""" 'c' """
      case "Short"   => q"1.asInstanceOf[Short]"
      case "Byte"    => q"1.asInstanceOf[Byte]"
      case "Any"     => q"1.asInstanceOf[Any]"
      case "AnyRef"  => q"1.asInstanceOf[AnyRef]"
      case "Object"  => q"1.asInstanceOf[Object]"
      case x         => q"""${newTermName(x)}(..${getApplyParams(x)})"""
    }

  }

  def asParameterizedDefaultParam(fieldTypeName: String) : Tree = {

    fieldTypeName match {
      //List
      case  l: String if l.startsWith("List[") => {
        if (getBoxed(l).endsWith("]")) q"""scala.List(${asParameterizedDefaultParam(getBoxed(l))})"""
        else q"""List(${asDefaultParam(getBoxed(l))})"""
      }
      //Option
      case  o: String if o.startsWith("Option[") => { 
        if(getBoxed(o).endsWith("]")) q"""Some(${asParameterizedDefaultParam(getBoxed(o))})"""
        else q"""Some(${asDefaultParam(getBoxed(o))})"""
      }
      //User-Defined
      case  x: String if x.startsWith(x + "[") => { 
        if(getBoxed(x).endsWith("]")) q"""${newTermName(x)}(${asParameterizedDefaultParam(getBoxed(x))})"""
        else q"""${newTermName(x)}(${asDefaultParam(getBoxed(x))})"""
      }
    }
  }

  def getBoxed(typeName: String) = {
    typeName.dropWhile( c => (c != '[') ).drop(1).dropRight(1)
  }
  
}
