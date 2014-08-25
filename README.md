toolbox-type-provider
=====================

Usage:
______

A runtime type-provider that gives case classes from strings, using standard (but still experimental) Scala reflection.


Add the following dependency: 
  
    "com.julianpeeters" %% "toolbox-type-provider" % "0.1"

Get a `ToolBoxCaseClass` instance with:

    import com.julianpeeters.toolbox.provider._
    import models.{FieldData, ClassData}

    val fieldData = List(FieldData("age", "Int"))
    val classData = ClassData(Some("mypackage"), "MyRuntimeClassName", fields)
    
    val tbcc = new ToolBoxCaseClass(classData)

with which you will be a able to:

    // get a `ClassSymbol` and enter Scala reflection
    tbcc.runtimeClassSymbol

    // get an instance of the newly generated class's companion object
    tbcc.runtimeCompanionObject

    // get an instance of the newly generated class
    tbcc.runtimeInstance

    // get an alias for the runtime type for use as a type parameter (in some contexts, ymmv)
    myParameterizedThing[tbcc.TYPE]

Please Note:
____________

1) The following Scala datatypes are supported:
    Boolean
    Long
    Float
    Double
    Short
    Int
    Byte
    Char
    String
    Unit
    Boolean
    Null
    Nothing
    AnyRef
    Any
    List
    Option
  other case classes*

2) Only the generation of case classes with fields but no body are supported. This is due to concerns about hygeine, specifically restricting the generation of anonymous classes that may pollute the namespace.

3) *Currently only one class may be generated per package due to [this error](https://github.com/julianpeeters/toolbox-salat-example/blob/two_classes_error/src/main/scala/Main.scala#L59), and thus nested case classes are not yet supported until this is overcome. 

4) The compile-time facility `typeOf[]` will only see the type alias or the dealiased `.type` call and not the underlying type that gets filled-in at runtime. Therefore entering Scala reflection via `typeOf[tbcc.TYPE]` does not work (unless somebody knows how to get a toolbox to generate a TypeTag at runtime?)

