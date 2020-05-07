package compiler.parser;

public enum ParserType {
	MAINPARSER("MainParser");

	String parserClassName;

	ParserType(String string) {
		this.parserClassName = string;
	}

	String getParserClassName() {
		return parserClassName;
	}

	static void init() {
		for (ParserType type : ParserType.values()) {
			try {
				Class.forName("compiler.parser." + type.getParserClassName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
