package MyInjector;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;
import soot.util.EmptyChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Injector_config extends BodyTransformer {

	final static String TMP_DIR_PATH = "/data/local/tmp/";

	public Injector_config() {
		super();
	}

	@Override
	protected void internalTransform(Body b, String s, Map<String, String> map) {

	}

}
