package com.julianpeeters.toolbox.provider

import models.FieldData
import stores.{ ClassStore, ClassFieldStore }

object FieldMatcher {

  def getBoxed(typeName: String) = {
    typeName.dropWhile( c => (c != '[') ).drop(1).dropRight(1)
  }

  def getExampleObject(typeName: String): Object = {
    typeName match {
      case "Null"    => null.asInstanceOf[Object]
      case "Boolean" => true.asInstanceOf[Object]
      case "Int"     => 1.asInstanceOf[Object]
      case "Long"    => 1L.asInstanceOf[Object]
      case "Float"   => 1F.asInstanceOf[Object]
      case "Double"  => 1D.asInstanceOf[Object]
      case "String"  => ""
      case "Byte"    => 1.toByte.asInstanceOf[Object]
      case "Short"   => 1.toShort.asInstanceOf[Object]
      case "Char"    => 'k'.asInstanceOf[Object]
      case "Any"     => "".asInstanceOf[Any].asInstanceOf[Object]
      case "AnyRef"  => "".asInstanceOf[AnyRef].asInstanceOf[Object]
      case "Unit"    => ().asInstanceOf[scala.runtime.BoxedUnit]
      case "Nothing" => null
      case "Object"  => new Object

      case l: String if l.startsWith("List[") => List(getExampleObject(getBoxed(l))).asInstanceOf[List[Any]].asInstanceOf[Object]
      case o: String if o.startsWith("Option[") => Option(getExampleObject(getBoxed(o))).asInstanceOf[Option[Any]].asInstanceOf[Object]
      case u: String =>  ClassStore.generatedClasses.get(typeName).get.runtimeInstance 
    }
  }

  def getReturnType(FieldDatas: List[FieldData]) = {
    FieldDatas.map(n => n.fieldType).map(m => m match { 
      case "Boolean" => classOf[Boolean]
      case "Int"     => classOf[Int]
      case "Long"    => classOf[Long]
      case "Float"   => classOf[Float]
      case "Double"  => classOf[Double]
      case "bytes"   => classOf[Seq[Byte]]
      case "String"  => classOf[String]
      case "Short"   => classOf[Short]
      case "Byte"    => classOf[Byte]
      case "Char"    => classOf[Char]
      case "Any"     => classOf[Any]
      case "AnyRef"  => classOf[AnyRef]
      case "Unit"    => classOf[scala.runtime.BoxedUnit]//classOf[Unit]
      case "Nothing" => classOf[Nothing]
      case "Null"    => classOf[Null]
      case "Object"  => classOf[Object]
      //Complex ------------------------

      case "enum"    => classOf[Enumeration#Value]
      case "array"   => classOf[Seq[_]]
      case "map"     => classOf[Map[String, _]]

      case l: String if l.startsWith("List[") => classOf[List[Any]]         
      case o: String if o.startsWith("Option[") => classOf[Option[Any]]         
      case x: String => ClassStore.generatedClasses.get(x).get.runtimeInstance.getClass
    })
  }
}
