package com.julianpeeters.toolbox.provider
import models.{ ClassData, FieldData }
import stores.{ ClassStore, ClassFieldStore }

// Adapted from a Gist courtesy Eugene Burmako: https://gist.github.com/5845539.git
import scala.reflect.runtime.{universe => ru}
import scala.tools.reflect.ToolBox
import scala.language.reflectiveCalls

class ToolBoxCaseClass(classData: ClassData) {

   def define(tb: ToolBox[ru.type], tree: ru.ImplDef, namespace: String): ru.Symbol = {
    val compiler = tb.asInstanceOf[{ def compiler: scala.tools.nsc.Global }].compiler
    val importer = compiler.mkImporter(ru)
    val exporter = importer.reverse
    val ctree: compiler.ImplDef = importer.importTree(tree).asInstanceOf[compiler.ImplDef]
    def defineInternal(ctree: compiler.ImplDef): compiler.Symbol = {
      import compiler._
 

    //  if we check the classloaders to prevent loading a class with an unhygienic name,
    //  and if we also restrict the members of the class to be fields of primitives,
    //  then no anonymous classes will be generated and interfere with compile-time gen'd anonymous classes,
    //  and so it's therefore safe to use a familiar namespace and not a unique one as is otherwise req'd.
      val packageName = newTermName(namespace)
      //  val packageName = newTermName("__wrapper$" + java.util.UUID.randomUUID.toString.replace("-", ""))
      val pdef = PackageDef(Ident(packageName), List(ctree))
      val unit = new CompilationUnit(scala.reflect.internal.util.NoSourceFile)
      unit.body = pdef
 
      val run = new Run
      reporter.reset()
      run.compileUnits(List(unit), run.namerPhase)
      compiler.asInstanceOf[{ def throwIfErrors(): Unit }].throwIfErrors()
 
      ctree.symbol
    }
    val csym: compiler.Symbol = defineInternal(ctree)
    val usym = exporter.importSymbol(csym)
    usym
  }
 
  import scala.reflect.runtime.universe._
  import Flag._
  import scala.reflect.runtime.{currentMirror => cm}

  val isImmutable: Boolean = true
  val name = newTypeName(classData.name)
  val namespace = classData.packageName match {
    case Some(n) => n
    case None    => "<empty>" 
  }
  val fullName = namespace + "." + name

  // Store fields so we know how to generate default params if this class appears as a member to another.
  ClassFieldStore.storeClassFields(classData)

  // Prep fields for splicing by mapping each to a quasiquote
  val fieldData: List[FieldData] = classData.fields
  val fields: List[Tree] = classData.fields.map(f => Quasiquoter.quotifyField(f, isImmutable) ) 

  def cdef() = q"case class $name(..$fields)"
  def newc(csym: Symbol) = q"""${csym.companionSymbol}"""
  val tb = cm.mkToolBox()
  val loader = (tb.asInstanceOf[scala.tools.reflect.ToolBoxFactory$ToolBoxImpl].classLoader)
  val runtimeClassSymbol = define(tb, cdef(), namespace)
  //val runtimeClassSymbol = tb.define(cdef()) // for 2.11, but wraps in a uniquely named package
  val runtimeCompanionObject = tb.eval(newc(runtimeClassSymbol))

  //using data from the input, prepare the args for reflective instantiation of the yet-to-be-gen'd class
  val methodParams: List[Class[_]] = FieldMatcher.getReturnType(fieldData).asInstanceOf[List[Class[_]]] 
  val instantiationParams: List[Object] = fieldData.map(fd => FieldMatcher.getExampleObject(fd.fieldType))
  val apply = runtimeCompanionObject.getClass.getMethod("apply", methodParams:_*)
  val runtimeInstance = apply.invoke(runtimeCompanionObject, instantiationParams:_*)

  type TYPE = runtimeInstance.type

}
