package compiler;

import java.io.File;
import java.util.List;

import compiler.ast.Node;
import compiler.ast.ProgramNode;
import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.TokenGenerator;
import compiler.parser.Parser;
import compiler.visitor.PrettyPrinter;
import compiler.visitor.SymbolVisitor;

public class Main {

	public static void main(String[] args) {
//		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//		String is = classloader.getResource("test1.txt").getFile();

		Lexer lex = new TokenGenerator(new File(args[0]));
		List<Token> tokenL = lex.lex();
		if (tokenL != null) {
			//tokenL.forEach(e -> System.out.println(e.getTokenType() + " " + e.getTokenValue()));

			Parser p = new Parser(tokenL);
			Node root = p.parse();

			//PrettyPrinter print = new PrettyPrinter();
			//print.visit((ProgramNode) root);
			
			SymbolVisitor sym = new SymbolVisitor();
			sym.visit((ProgramNode) root);
		}
	}

}
