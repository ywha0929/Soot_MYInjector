package MyInjector;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;
import soot.util.EmptyChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import MyInjector.AndroidUtil;
import MyInjector.InstrumentUtil;

public class Injector_config extends BodyTransformer {

	final static String TMP_DIR_PATH = "/data/local/tmp/";
	final static String INJECTOR_ADDON_PATH = "/data/local/tmp/InjectorAddOn.apk";
	final static String ADD_ON_MAIN_CLASS = "com.example.injectoraddon.InjectorAddOn";
	static boolean isClassAnalize = true;
	static boolean isMethodAnalize = true;
	static boolean isInsert = true;
	static int MAINACTIVITY_INDEX = 1386;

	public Injector_config() {
		super();
	}

	@Override
	protected void internalTransform(Body b, String s, Map<String, String> map) {
		JimpleBody body = (JimpleBody) b;
		if (AndroidUtil.isAndroidMethod(b.getMethod()))
			return;

		if (isClassAnalize) {
			isClassAnalize = false;
			printClasses(body);
		}
		if (isMethodAnalize) {
			printMethods(body);
		}

		if (isInsert) {
			isInsert = false;
			Object[] arr = Scene.v().getApplicationClasses().toArray();
			SootClass a = (SootClass) arr[MAINACTIVITY_INDEX];// MainActivity

			SootField testField = InstrumentUtil.addField(a, "dex", RefType.v("dalvik.system.DexClassLoader"),
					Modifier.PUBLIC | Modifier.STATIC);

		}

		if (b.getMethod().getName().equals("onCreate")) {
			System.out.println("==== before ====");
			System.out.println(b);
			inject_onCreate((JimpleBody) b);
			// injectfield((JimpleBody) b);
			// injectClassLoader((JimpleBody) b);
			System.out.println("==== after ====");
			System.out.println(b);
		}
		if (b.getMethod().getName().equals("onClick")) {
			System.out.println("==== before ====");
			System.out.println(b);
			inject_onClick((JimpleBody) b);
			// injectfield((JimpleBody) b);
			// injectClassLoader((JimpleBody) b);
			System.out.println("==== after ====");
			System.out.println(b);
		}
	}

	void inject_onCreate(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();

		// local variables
		Local thisVar = body.getThisLocal();
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.ClassLoader"));

		// create DexClassLoader instance
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Object",
				"java.lang.Class getClass()", thisVar, classVar));
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.ClassLoader getClassLoader()", classVar, classLoaderVar));
		generated.addAll(InstrumentUtil.generateNewInstance(body, "dalvik.system.DexClassLoader",
				"void <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.ClassLoader)", dexLoaderVar,
				StringConstant.v(INJECTOR_ADDON_PATH), StringConstant.v(TMP_DIR_PATH), NullConstant.v(),
				classLoaderVar));
		// copy dexLoaderVar to this.dex field
		Object[] arr = Scene.v().getApplicationClasses().toArray();
		SootClass a = (SootClass) arr[MAINACTIVITY_INDEX];
		SootField ar = a.getFieldByName("dex");
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(ar.makeRef()), dexLoaderVar));

		units.insertBefore(generated, units.getLast());

		// validate the instrumented code
		body.validate();
	}

	void inject_onClick(JimpleBody body) {
		UnitPatchingChain units = body.getUnits();
		List<Unit> generated = new ArrayList<>();
		printLocals(body);
		Local exceptionVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Exception"));
		Local methodVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.reflect.Method"));
		Local dexLoaderVar = InstrumentUtil.generateNewLocal(body, RefType.v("dalvik.system.DexClassLoader"));
		Local classVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Class"));
		Local classArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Class"), 1));
		Local objectArrayVar = InstrumentUtil.generateNewLocal(body, ArrayType.v(RefType.v("java.lang.Object"), 1));
		Local resultObjectVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Object"));
		Local resultVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.Float"));
		Local edit1Var = InstrumentUtil.generateNewLocal(body, RefType.v("android.widget.EditText"));
		Local edit2Var = InstrumentUtil.generateNewLocal(body, RefType.v("android.widget.EditText"));
		Local edit3Var = InstrumentUtil.generateNewLocal(body, RefType.v("android.widget.EditText"));
//		Local thisMainActivityVar = InstrumentUtil.generateNewLocal(body, RefType.v("com.example.testapp.MainActivity"));
		Local resultCharseqVar = InstrumentUtil.generateNewLocal(body, RefType.v("java.lang.CharSequence"));
		//get MainActivity$1 Var
		Object[] localArr = body.getLocals().toArray();
		Local thisMainActivityVar = (Local)localArr[4];
		// get dexloader and EditText from field
		Object[] classArr = Scene.v().getApplicationClasses().toArray();
		SootClass classMainActivity = (SootClass) classArr[MAINACTIVITY_INDEX];
		SootField dexVar = classMainActivity.getFieldByName("dex");
//		SootField edit
		generated.add(Jimple.v().newAssignStmt(dexLoaderVar, Jimple.v().newStaticFieldRef(dexVar.makeRef())));
		
		
//		generated.add(Jimple.v().newAssignStmt(thisMainActivityVar, body.getThisLocal()));

		generated.addAll(InstrumentUtil.generateStaticInvokeStmt(body, "com.example.testapp.MainActivity", "android.widget.EditText access$000(com.example.testapp.MainActivity)", edit1Var, thisMainActivityVar));
		generated.addAll(InstrumentUtil.generateStaticInvokeStmt(body, "com.example.testapp.MainActivity", "android.widget.EditText access$100(com.example.testapp.MainActivity)", edit2Var, thisMainActivityVar));
		generated.addAll(InstrumentUtil.generateStaticInvokeStmt(body, "com.example.testapp.MainActivity", "android.widget.EditText access$200(com.example.testapp.MainActivity)", edit3Var, thisMainActivityVar));

		
		
//		gener
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.ClassLoader",
				"java.lang.Class loadClass(java.lang.String)", dexLoaderVar, classVar,
				StringConstant.v(ADD_ON_MAIN_CLASS)));
		Unit tryBegin = generated.get(generated.size() - 1);

		// create Class array for getDeclaredMethod
		SootClass cls = Scene.v().getSootClass("java.lang.Class");
		generated.add(
				Jimple.v().newAssignStmt(classArrayVar, Jimple.v().newNewArrayExpr(cls.getType(), IntConstant.v(2))));

		// put class to class array

		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(0)),
				ClassConstant.v("Landroid/widget/EditText;")));
		generated.add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(classArrayVar, IntConstant.v(1)),
				ClassConstant.v("Landroid/widget/EditText;")));
		// get runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Class",
				"java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])", classVar, methodVar,
				StringConstant.v("runtest"), classArrayVar));
		
		// create object array for invoke
		SootClass cls2 = Scene.v().getSootClass("java.lang.Object");
		generated.add(
				Jimple.v().newAssignStmt(objectArrayVar, Jimple.v().newNewArrayExpr(cls2.getType(), IntConstant.v(2))));
		generated.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(0)), edit1Var));
		generated.add(
				Jimple.v().newAssignStmt(Jimple.v().newArrayRef(objectArrayVar, IntConstant.v(1)), edit2Var));
		// invoke runtest
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.reflect.Method",
				"java.lang.Object invoke(java.lang.Object,java.lang.Object[])", methodVar, resultObjectVar, NullConstant.v(),
				objectArrayVar));
		
		generated.add(Jimple.v().newAssignStmt(resultVar, Jimple.v().newCastExpr(resultObjectVar, resultVar.getType())));
		//prepare result Stringf
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Float", "java.lang.String toString()", resultVar, resultCharseqVar));
		// edit3.setText
		generated.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "android.widget.TextView", "void setText(java.lang.CharSequence)", edit3Var, null, resultCharseqVar));
		units.insertBefore(generated, units.getPredOf(units.getLast()));
 
		Unit tryEnd = (units.getLast());
        // insert try-catch statement
        CaughtExceptionRef exceptionRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
        Unit catchBegin = Jimple.v().newIdentityStmt(exceptionVar, exceptionRef);
        units.add(catchBegin);
        units.addAll(InstrumentUtil.generateVirtualInvokeStmt(body, "java.lang.Throwable",
            "void printStackTrace()", exceptionVar, null));
        units.add(Jimple.v().newReturnVoidStmt());
        SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
        Trap trap = soot.jimple.Jimple.v().newTrap(exceptionClass, tryBegin, tryEnd, catchBegin);
        body.getTraps().add(trap);
        System.out.println(body.toString());
        printLocals(body);
		// validate the instrumented code
		body.validate();
	}

	void printClasses(JimpleBody body) {
		Object[] arr = Scene.v().getApplicationClasses().toArray();
		for (int i = 0; i < arr.length; i++) {
			System.out.println("Class [" + i + "] : " + arr[i].toString());
		}
	}

	void printMethods(JimpleBody body) {
		System.out.println(body.getMethod().toString());
	}

	void printLocals(JimpleBody body) {
		Object[] arr = body.getLocals().toArray();
		for (int i = 0; i < arr.length; i++) {
			System.out.println("Local [" + i + "] : " + arr[i].toString());
		}
	}
}
