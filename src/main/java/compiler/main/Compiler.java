package compiler.main;

import compiler.analyzer.PrettyPrinter;
import compiler.analyzer.SemanticsChecker;
import compiler.ast.Node;
import compiler.ast.Node.ProgramNode;
import compiler.cafelang.ir.CafeModule;
import compiler.gen.ASTToCafeIrVisitor;
import compiler.gen.JVMByteCodeGen;
import compiler.gen.SymbolReferenceAssignment;
import compiler.parser.Parser;
import compiler.parser.ParserFactory;
import compiler.parser.ParserType;
import compiler.util.Context;
import compiler.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Compiler {
	protected static final Context.Key<Compiler> compilerKey = new Context.Key<>();

	private ParserFactory parserFactory;
	private Log log;
	private SourceFileManager fileManager;
	private final String classFileName;

	private Parser parser;
	private SemanticsChecker analyzer;
	
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

		String fileName = fileManager.getSourceFileName();
		classFileName = fileName.substring(0,fileName.lastIndexOf('.'));

		parserFactory = ParserFactory.instance(context);
		parser = parserFactory.newParser(ParserType.MAINPARSER, fileManager.getSourceFileCharList());
		
		analyzer = SemanticsChecker.instance(context);
	}

	enum Phase{
		PARSE,
		ANALYZE,
		IR,
		GEN
	}
	
	boolean checkErrors() {
		if(log.nerrors > 0) {
			return true;
		}
		return false;
	}
	
	public void compile() {
		Node programNode = null;
		CafeModule module = null;
		byte[] byteCode = null;
	    for(Phase phase : Phase.values()) {
			switch(phase) {
				case PARSE:
					programNode = parser.parse();
					break;
				case ANALYZE:
					System.out.println((char)27+"[33m"+"\nPrettyPrint");
					new PrettyPrinter().prettyPrint(programNode);
					analyzer.visitProgram((ProgramNode)programNode);
					break;
				case IR:
					module = new ASTToCafeIrVisitor().transform((ProgramNode)programNode,classFileName);
					module.accept(new SymbolReferenceAssignment());
					break;
				case GEN:
					byteCode = new JVMByteCodeGen().generateByteCode(module,classFileName);
					File op = new File(classFileName+".class");
					try (FileOutputStream out = new FileOutputStream(op)) {
						out.write(byteCode);
					}
					catch (IOException e){
						e.printStackTrace();
					}
			}
			if(checkErrors()) {
				return;
			}
		}
	}
}
