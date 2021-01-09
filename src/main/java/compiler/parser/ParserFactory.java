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

package compiler.parser;

import compiler.util.Context;
import compiler.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A factory to generate parsers. To register a new concrete parser the
 * following steps have to be followed:
 * <ol>
 * <li>Create a concrete class that implements <code>Parser</code> interface.
 * <li>Add the parser name in the <code>ParserType</code> with the class name of
 * the above created new parser as its argument.
 * <li>In the newly created parser class, inside static block, call the method
 * <code>ParserFactory.registerParser(...)</code>, to register the newly created
 * parser.
 * </ol>
 *
 * @author Dhyey
 */
public class ParserFactory {
    protected final static Context.Key<ParserFactory> parserFactoryKey = new Context.Key<>();

    private final static Map<ParserType, Parser> parsers = new HashMap<>();

    final Log log;
    final ScannerFactory scannerFactory;

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

        this.log = Log.instance(context);
        this.scannerFactory = ScannerFactory.instance(context);
    }

    public Parser newParser(ParserType type, List<Character> input) {
        Parser p = parsers.get(type);
        if (p == null) {
            throw new AssertionError("Parser of type " + type + " is not registered");
        }

        Lexer lexer = scannerFactory.newScanner(input);

        return p.instance(this, lexer);
    }
}
