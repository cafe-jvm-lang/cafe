package compiler.main;

import compiler.parser.Parser;
import compiler.parser.ParserFactory;
import compiler.parser.ParserType;
import compiler.util.Context;
import compiler.util.Log;

public class Compiler {
	protected static final Context.Key<Compiler> compilerKey = new Context.Key<>();

	private ParserFactory parserFactory;
	private Log log;

	public static Compiler instance(Context context) {
		Compiler instance = context.get(compilerKey);
		if (instance == null)
			instance = new Compiler(context);
		return instance;
	}

	private Compiler(Context context) {
		context.put(compilerKey, this);

		log = Log.instance(context);
		parserFactory = ParserFactory.instance(context);

		Parser p = parserFactory.newParser(ParserType.MAINPARSER);
	}

	public void compile() {

	}
}
