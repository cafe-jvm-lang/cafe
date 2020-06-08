package compiler.parser;

public abstract class Parser {
	abstract Parser instance(ParserFactory factory, Lexer lexer);
}