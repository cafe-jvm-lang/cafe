package compiler.parser;

import java.util.HashMap;
import java.util.Map;

import compiler.util.Context;

public class ParserFactory {
	protected final static Context.Key<ParserFactory> parserFactoryKey = new Context.Key<>();

	private final static Map<ParserType, Parser> parsers = new HashMap<>();

	public static ParserFactory instance(Context context) {
		ParserFactory instance = context.get(parserFactoryKey);

		if (instance == null) {
			instance = new ParserFactory(context);
		}

		return instance;
	}

	public static void registerParser(ParserType type, Parser parser) {
		Object o = parsers.put(type, parser);
		if (o != null) {
			throw new AssertionError("Parser of type " + type + " is already Registered");
		}
	}

	private ParserFactory(Context context) {
		context.put(parserFactoryKey, this);
		ParserType.init();
	}

	public Parser newParser(ParserType type) {
		Parser p = parsers.get(type);
		if (p == null) {
			throw new AssertionError("Parser of type " + type + " is not registered");
		}

		return p.instance(this);
	}
}
