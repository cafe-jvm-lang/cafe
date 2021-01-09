/*
 * Copyright (c) 2021. Dhyey Shah <dhyeyshah4@gmail.com> 
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

package compiler.util;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.text.MessageFormat;
import java.util.HashMap;

import static compiler.util.Log.Type.*;
import static org.fusesource.jansi.Ansi.ansi;

public final class Messages {
    private static final String ERROR = "\u001B[31m"; // red
    private static final String INFO = "\u001B[34m";  // blue
    private static final String WARNING = "\u001B[33m"; // yellow
    private static final String SUCCESS = "\u001B[36m"; // cyan

    private static final Ansi.Color JERROR = Ansi.Color.RED;
    private static final Ansi.Color JSUCCESS = Ansi.Color.GREEN;

    private static final HashMap<Log.Type, String> MESSAGES;

    static {
        MESSAGES = new HashMap<Log.Type, String>() {{
            put(Log.Type.SUCCESS, "SUCCESS");
            put(Log.Type.ERROR, "ERROR");
            put(Log.Type.WARNING, "WARNING");

            // Position
            put(SOURCE_POSITION, "at <line={0}, column={1}>");

            // CLI Errors
            put(INVALID_CLI_FILE_PATH, "Path `{0}` does not exists");
            put(MODULE_NOT_FOUND, "Module `{0}` not found");

            // Lexical Errors
            put(NO_FILE_PATH_GIVEN_IN_CLI, "No source file provided to compile");
            put(INVALID_IDENTIFIER, "Invalid identifier `{0}`");
            put(EOF_PARSING_COMMENT, "EOF while parsing comment");
            put(EOF, "Unexpected end of file");
            put(ILLEGAL_CHARACTER, "Illegal character {0}");

            // Parsing Errors
            put(SYMBOL_EXPECTED, "`{0}` expected, found `{1}`");
            put(INVALID_EXPRESSION, "Invalid expression");

            // Semantic Errors
            put(DUPLICATE_SYMBOL, "Duplicate symbol `{0}`");
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
        AnsiConsole.systemInstall();
        System.err.println(prefixed(prefix, message, color));
    }

    public static void printPrefixed(Log.Type prefix, String message, Ansi.Color color) {
        AnsiConsole.systemInstall();
        System.out.println(
                ansi().fg(color).a("["+MESSAGES.get(prefix)+"] ").reset().a(message)
        );
        AnsiConsole.systemUninstall();
    }

    public static void error(Object message) {
        printPrefixed(Log.Type.ERROR, String.valueOf(message), JERROR);
    }

    public static void success(Object message) {
        printPrefixed(Log.Type.SUCCESS, String.valueOf(message), JSUCCESS);
    }
}
