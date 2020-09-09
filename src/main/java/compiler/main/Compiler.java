package compiler.main;

import compiler.analyzer.PrettyPrinter;
import compiler.ast.Node;
import compiler.parser.Parser;
import compiler.parser.ParserFactory;
import compiler.parser.ParserType;
import compiler.util.Context;
import compiler.util.Log;

public class Compiler {
	protected static final Context.Key<Compiler> compilerKey = new Context.Key<>();

	private ParserFactory parserFactory;
	private Log log;
	private SourceFileManager fileManager;
	
	private Parser parser;
	
	public static Compiler instance(Context context) {
		Compiler instance = context.get(compilerKey);
		if (instance == null)
			instance = new Compiler(context);
		return instance;
	}

	private Compiler(Context context) {
		context.put(compilerKey, this);

		log = Log.instance(context);
		fileManager = SourceFileManager.instance(context);
		
		parserFactory = ParserFactory.instance(context);
		parser = parserFactory.newParser(ParserType.MAINPARSER, fileManager.getSourceFileCharList());
	}

	enum Phase{
		PARSE,
		ANALYZE
	}
	
	boolean checkErrors() {
		if(log.nerrors > 0) {
			return true;
		}
		return false;
	}
	
	public void compile() {
		Node programNode = null;
	    for(Phase phase : Phase.values()) {
			switch(phase) {
				case PARSE:
					programNode = parser.parse();
					break;
				case ANALYZE:
					System.out.println("PrettyPrint");
					new PrettyPrinter().prettyPrint(programNode);
					break;
			}
			if(checkErrors()) {
				return;
			}
		}
	}
}
