# Welcome to soot injector tutorial!
* This project is based on project by noidsirius
* https://github.com/noidsirius/SootTutorial

## apks Directory
* holds apk files for instrumentation.
* input.apk : simple app. When you click the button, it adds two numbers and prints it.
* InjectorAddOn.apk : Contains runtest method. used for simplifying code injection. Must be loaded by dexclassloader.

## output Directory
* After running, output apk file is located here.

## src/main/java/MyInjector Directory
* directory where source code files are located.

### AndroidUtil.java
* methods to support Android system.

### InstrumentUtil.java
* methods for instrumenting.

### MyInjector.java
* holds main class

### Injector_config.java
* specifies how the apk file should be injected.

## Method Introduction (InstrumentUtil)
### public static Local generateNewLocal(Body body, Type type)
* Generates new local variable inside 'body' with type 'type'

### public static List<Unit> generateNewInstance(Body body, String clsName, String signature, Local base, Value... args)
* Generates new instance of class 'clsName', using initalizer 'signature', with parameters 'args', at variable 'base, inside 'body'
  
### public static SootField addField(SootClass cls, String name, Type type, int modifier)
* Creates new Field with name 'name', with type 'type', at class 'cls', with modifier 'modifier'
* Sootclass cls can be easily found by Scene.v().getApplicationClasses()
  
### public static List<Unit> generateStaticInvokeStmt(Body body, String clsName, String signature, Local retVar, Value... args)
* Generates new bytecode for invoking a **Static** method.
* Generates at 'body', using method 'signature', implemented at 'clsName', and return value is saved at 'retVar', and parameter given with 'args'
  
### public static List<Unit> generateVirtualInvokeStmt(Body body, String clsName, String signature, Local base, Local retVar, Value... args)
* Generates new bytecode for invoking a **Static** method.
* Generates at 'body', using method 'signature', implemented at 'clsName', and return value is saved at 'retVar', and parameter given with 'args'
* Runs method with instance 'base'

## Method Introduction (Jimple.v())
### newAssignStmt(Value Lvalue, Value Rvalue)
* Generates new assign statement. (Lvalue = Rvalue)
  
### newNewArrayExpr(Type, Int)
* Generates New array reference, and returns it.
  
### newArrayRef(Value array, int index)
* Makes Reference of the array at index
  
## Review
* It's easy to use with DexClassLoader
* But its Documentation is poor.
* Because of increasing popularity of kotlin in Android programing, it might be more cool if soot supports kotlin language.

##### Another project using Soot
* https://github.com/ywha0929/FLUID_UseCase
