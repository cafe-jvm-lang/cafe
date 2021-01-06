package compiler.parser;

/**
 * A registry of the concrete implementation of parsers.
 *
 * @author Dhyey
 */
public enum ParserType {
    MAINPARSER("MainParser");

    String parserClassName;

    ParserType(String className) {
        this.parserClassName = className;
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
