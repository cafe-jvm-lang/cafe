package compiler.main;

import compiler.analyzer.PrettyPrinter;
import compiler.analyzer.SemanticsChecker;
import compiler.ast.Node;
import compiler.ast.Node.ProgramNode;
import cafelang.ir.CafeModule;
import compiler.gen.ASTToCafeIrVisitor;
import compiler.gen.JVMByteCodeGenVisitor;
import compiler.gen.SymbolReferenceAssignmentVisitor;
import compiler.parser.Parser;
import compiler.parser.ParserFactory;
import compiler.parser.ParserType;
import compiler.util.Context;
import compiler.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Compiler {
	protected static final Context.Key<Compiler> compilerKey = new Context.Key<>();

	private ParserFactory parserFactory;
	private Log log;
	private SourceFileManager fileManager;
	private final String moduleName;

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
		moduleName = fileName.substring(0,fileName.lastIndexOf('.'));

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
		if(log.entries() > 0) {
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
					module = new ASTToCafeIrVisitor().transform((ProgramNode)programNode, moduleName);
					module.accept(new SymbolReferenceAssignmentVisitor());
					break;
				case GEN:
					byteCode = new JVMByteCodeGenVisitor().generateByteCode(module, moduleName);
					File op = new File(moduleName +".class");
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
