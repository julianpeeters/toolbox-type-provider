

import com.julianpeeters.toolbox.provider._
import models.{ ClassData, FieldData }
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._


import org.specs2._
import mutable._
import specification._

class FloatFloatSpec extends mutable.Specification {

  val valueMembers: List[FieldData] = List(FieldData("a","Float"), FieldData("b","Float"))
  val classData = ClassData(Some("floatfloatspec"), "MyRecord_FloatFloatSpec", valueMembers)
  val tbcc = new ToolBoxCaseClass(classData)

  val typeTemplate = tbcc.runtimeInstance

  type MyRecord = typeTemplate.type

  ctx.registerClassLoader(tbcc.loader)

  val dbo = grater[MyRecord].asDBObject(typeTemplate)
    println(dbo)

  val obj = grater[MyRecord].asObject(dbo)
    println(obj)
 
 "given a dynamically generated case class MyRecord_FloatFloatSpec(a: Float, b: Float) as a type parameter, a grater" should {
    "serialize and deserialize correctly" in {
      typeTemplate === obj
    }
}



}
