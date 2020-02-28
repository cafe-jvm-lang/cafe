package compiler;

import java.io.File;
import java.util.List;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.TokenGenerator;
import compiler.parser.Parser;

public class Main {

	public static void main(String[] args) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		String is = classloader.getResource("test.txt").getFile();

		Lexer lex = new TokenGenerator(new File(is));
		List<Token> tokenL = lex.lex();
		tokenL.forEach(e -> System.out.println(e.getTokenType()+" "+e.getTokenValue()));
		
		Parser p = new Parser(tokenL);
		p.parse();
	}

}
