package compiler.parser;

public class MainParser extends Parser{
	static {
		ParserFactory.registerParser(ParserType.MAINPARSER, new MainParser());
	}

	private MainParser() {
	}

	private MainParser(ParserFactory factory) {
		System.out.println("PARSING");
	}

	@Override
	protected MainParser instance(ParserFactory factory) {
		return new MainParser(factory);
	}
}
