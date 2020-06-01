package compiler.parser;

public class MainParser extends Parser {
	static {
		ParserFactory.registerParser(ParserType.MAINPARSER, new MainParser());
	}

	private Lexer lexer;
	
	private MainParser() {
	}

	private MainParser(ParserFactory factory, Lexer lexer) {
		System.out.println("PARSING");
		this.lexer = lexer;
	}

	@Override
	protected MainParser instance(ParserFactory factory,Lexer lexer) {
		return new MainParser(factory,lexer);
	}
}
