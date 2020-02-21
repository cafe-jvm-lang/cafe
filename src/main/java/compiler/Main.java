package compiler;

import java.io.File;

import compiler.lexer.Lexer;
import compiler.lexer.TokenGenerator;

public class Main {

	public static void main(String[] args) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		String is = classloader.getResource("test.txt").getFile();

		Lexer lex = new TokenGenerator(new File(is));
		lex.lex().forEach(e -> System.out.println(e.getTokenType()+" "+e.getTokenValue()));
	}

}
