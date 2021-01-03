package compiler.util;

import java.text.MessageFormat;
import java.util.HashMap;

import static compiler.util.Log.Type.*;

public final class Messages {
    private static final String ERROR = "\u001B[31m"; // red
    private static final String INFO = "\u001B[34m";  // blue
    private static final String WARNING = "\u001B[33m"; // yellow
    private static final String STACK = "\u001B[36m"; // cyan

    private static final HashMap<Log.Type, String> MESSAGES;

    static {
        MESSAGES = new HashMap<>() {{

            put(Log.Type.ERROR, "ERROR");
            put(Log.Type.WARNING, "WARNING");

            // Position
            put(SOURCE_POSITION, "at <line={0}, column={1}>");

            // CLI Errors
            put(INVALID_CLI_FILE_PATH, "Path `{0}` does not exists");

            // Lexical Errors
            put(NO_FILE_PATH_GIVEN_IN_CLI, "No source file provided to compile");
            put(INVALID_IDENTIFIER, "Invalid identifier `{0}`");
            put(EOF_PARSING_COMMENT, "EOF while parsing comment");
            put(EOF, "Unexpected end of file");
            put(ILLEGAL_CHARACTER, "Illegal character {0}");

            // Parsing Errors
            put(SYMBOL_EXPECTED, "`{0}` expected, found `{1}`");

            // Semantic Errors
            put(DUPLICATE_SYMBOL, "Symbol {0} is already declared");
            put(SYMBOL_NOT_DECLARED, "Undeclared symbol {0}");
            put(LHS_EXPR_ERROR, "Illegal LHS expression `{0}`");
            put(REASSIGN_CONSTANT, "Cannot reassign constant `{0}`");
            put(RETURN_OUTSIDE_BLOCK, "Return statement outside function");
        }};

    }

    private Messages() {
    }

    public static String message(Log.Type key, Object... values) {
        return MessageFormat.format(MESSAGES.get(key), values);
    }

    public static String prefixed(Log.Type prefix, String message, String color) {
        return String.format("[%s%s\u001B[0m] %s", color, MESSAGES.get(prefix), message);
    }

    public static void printPrefixed(Log.Type prefix, String message, String color) {
        System.err.println(prefixed(prefix, message, color));
    }

    public static void error(Object message) {
        printPrefixed(Log.Type.ERROR, String.valueOf(message), ERROR);
    }
}
