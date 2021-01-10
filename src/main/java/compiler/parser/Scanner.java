/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
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

import compiler.parser.Tokens.Token;

import java.util.List;

public class Scanner implements Lexer {

    static {
        ScannerFactory.registerScanner(new Scanner());
    }

    private Tokens tokens;
    private Token token;
    private Token prevToken;

    private Tokenizer tokenizer;

    private Scanner() {
    }

    private Scanner(ScannerFactory scannerFactory, List<Character> buff) {
        this.tokens = scannerFactory.tokens;
        token = prevToken = null;

        tokenizer = new Tokenizer(scannerFactory, buff);
    }

    protected Scanner instance(ScannerFactory scannerFactory, List<Character> input) {
        return new Scanner(scannerFactory, input);
    }

    @Override
    public void nextToken() {
        prevToken = token;
        token = tokenizer.readToken();
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public Token prevToken() {
        return prevToken;
    }
}
