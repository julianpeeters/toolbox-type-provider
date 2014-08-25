//Semi- Useless Test: shows that my class passes the verifier, but java.lang.Object will be a custom class, and will be a case class model with a toString that works

import com.julianpeeters.toolbox.provider._
import models.{ ClassData, FieldData }
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._


import org.specs2._
import mutable._
import specification._

class ObjectObjectSpec extends mutable.Specification {

  val valueMembers: List[FieldData] = List(FieldData("a","Object"), FieldData("b","Object"))
  val classData = ClassData(Some("objectobject"), "MyRecord_ObjectObjectSpec", valueMembers)
  val tbcc = new ToolBoxCaseClass(classData)

  val typeTemplate = tbcc.runtimeInstance

  type MyRecord = typeTemplate.type
  ctx.registerClassLoader(tbcc.loader)
  val dbo = grater[MyRecord].asDBObject(typeTemplate)
   // println(dbo)

  val obj = grater[MyRecord].asObject(dbo)
    println(obj)

 "given a dynamically generated case class MyRecord(a: Object, b: Object) as a type parameter, a grater" should {
    "serialize and deserialize correctly" in {
      typeTemplate === obj

    }
}



}
