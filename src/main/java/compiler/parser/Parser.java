package compiler.parser;

import compiler.ast.Node;

public abstract class Parser {
	abstract Parser instance(ParserFactory factory, Lexer lexer);
	
	public abstract Node parse();
}