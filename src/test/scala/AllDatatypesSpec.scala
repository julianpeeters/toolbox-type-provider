

import com.julianpeeters.toolbox.provider._
import models.{ ClassData, FieldData }
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._


import org.specs2._
import mutable._
import specification._

class AllDatatypesSpec extends mutable.Specification {

  val valueMembers: List[FieldData] = List(FieldData("a", "Byte"), 
    FieldData("b", "Short"), 
    FieldData("c", "Int"), 
    FieldData("d", "Long"), 
    FieldData("e", "Float"), 
    FieldData("f", "Double"), 
    FieldData("g", "Char"), 
    FieldData("h", "String"), 
    FieldData("i", "Boolean"), 
    FieldData("j", "Unit"), 
    FieldData("m", "Any"), 
    FieldData("n", "Byte"), 
    FieldData("o", "Object"))

  val classData = ClassData(Some("alldatatypesspec"), "MyRecord_AllDatatypesSpec", valueMembers)
  val tbcc = new ToolBoxCaseClass(classData)

  val typeTemplate = tbcc.runtimeInstance
  ctx.registerClassLoader(tbcc.loader)
  type MyRecord = typeTemplate.type

  val dbo = grater[MyRecord].asDBObject(typeTemplate)

  val obj = grater[MyRecord].asObject(dbo)
 
 "given a dynamically generated case class MyRecord_StringIntBooleanSpec(a: Int) as a type parameter, a grater" should {
    "serialize and deserialize correctly" in {
      typeTemplate === obj
    }
}



}
