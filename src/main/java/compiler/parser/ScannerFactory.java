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

import java.util.List;

public class ScannerFactory {
    public static final Context.Key<ScannerFactory> scannerFactoryKey = new Context.Key<>();

    final Log log;
    final Tokens tokens;

    private static Scanner scanner;

    static {
        try {
            Class.forName("compiler.parser.Scanner");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ScannerFactory instance(Context context) {
        ScannerFactory instance = context.get(scannerFactoryKey);
        if (instance == null)
            instance = new ScannerFactory(context);
        return instance;
    }

    private ScannerFactory(Context context) {
        context.put(scannerFactoryKey, this);
        log = Log.instance(context);
        tokens = Tokens.instance(context);
    }

    static void registerScanner(Scanner sc) {
        scanner = sc;
    }

    public Lexer newScanner(List<Character> input) {
        return scanner.instance(this, input);
    }

}
