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

import compiler.parser.Tokens.Token.Tag;
import compiler.util.Context;
import compiler.util.Position;

public class Tokens {

    public static final Context.Key<Tokens> tokensKey = new Context.Key<>();

    public static Tokens instance(Context context) {
        Tokens instance = context.get(tokensKey);

        if (instance == null)
            instance = new Tokens(context);

        return instance;
    }

    private Tokens(Context context) {
        context.put(tokensKey, this);
    }

    TokenKind lookupKind(String name) {
        switch (name) {


//		case "(":
//			return TokenKind.LPAREN;
//		case ")":
//			return TokenKind.RPAREN;
//		case "{":
//			return TokenKind.LCURLY;
//		case "}":
//			return TokenKind.RCURLY;
//		case "[":
//			return TokenKind.LSQU;
//		case "]":
//			return TokenKind.RSQU;
            case "list":
                return TokenKind.LIST;
            case "map":
                return TokenKind.MAP;
            case "link":
                return TokenKind.LINK;
            case "set":
                return TokenKind.SET;
            case "and":
                return TokenKind.AND;
//		case "&&":
//			return TokenKind.ANDOP;
            case "or":
                return TokenKind.OR;
//		case "||":
//			return TokenKind.OROP;
            case "not":
                return TokenKind.NOT;
//		case "!":
//			return TokenKind.NOTOP;	
//		case ">":
//			return TokenKind.GT;
//		case "<":
//			return TokenKind.LT;
//		case ">=":
//			return TokenKind.GTE;
//		case "<=":
//			return TokenKind.LTE;
//		case "!=":
//			return TokenKind.NOTEQU;
//		case "==":
//			return TokenKind.EQUEQU;
//		case "=":
//			return TokenKind.EQU;
//		case "|":
//			return TokenKind.BITOR;
//		case "&":
//			return TokenKind.BITAND;
//		case "<<":
//			return TokenKind.LSHIFT;
//		case ">>":
//			return TokenKind.RSHIFT;
//		case ">>>":
//			return TokenKind.TRSHIFT;
//		case "+":
//			return TokenKind.ADD;
//		case "-":
//			return TokenKind.SUB;
//		case "*":
//			return TokenKind.MUL;
//		case "/":
//			return TokenKind.DIV;
//		case "//":
//			return TokenKind.FLOORDIV;
//		case "%":
//			return TokenKind.MOD;
//		case "**":
//			return TokenKind.POWER;
            case "is":
                return TokenKind.IS;
            case "if":
                return TokenKind.IF;
            case "else":
                return TokenKind.ELSE;
//		case "is not":
//			return TokenKind.ISNOT;
            case "in":
                return TokenKind.IN;
            case "loop":
                return TokenKind.LOOP;
            case "for":
                return TokenKind.FOR;
//		case "'":
//			return TokenKind.SQOUTE;
//		case "\"":
//			return TokenKind.DQOUTE;	
//		case ".":
//			return TokenKind.DOT;
//		case "@":
//			return TokenKind.IMPORT;
//		case "#":
//			return TokenKind.SINGLECOMMENT;
//		case "/*":
//			return TokenKind.LCOMMENT;
//		case "*/":
//			return TokenKind.RCOMMENT;
            case "this":
                return TokenKind.THIS;
            case "null":
                return TokenKind.NULL;
            case "true":
                return TokenKind.TRUE;
            case "false":
                return TokenKind.FALSE;
            case "var":
                return TokenKind.VAR;
            case "const":
                return TokenKind.CONST;
            case "func":
                return TokenKind.FUNC;
            case "continue":
                return TokenKind.CONTINUE;
            case "return":
                return TokenKind.RET;
            case "break":
                return TokenKind.BREAK;
            default:
                return TokenKind.IDENTIFIER;
        }
    }

    /**
     * Add all tokens here
     */
    public enum TokenKind {
        SEMICOLON(";"),
        COMMA(","),
        TILDE("~"),
        COLON(":"),
        NUMLIT(Tag.NUMERIC),
        STRLIT(Tag.STRING),
        VAR("var", Tag.NAMED),
        CONST("const", Tag.NAMED),
        FUNC("func", Tag.NAMED),
        IDENTIFIER("identifier", Tag.NAMED),
        IF("if", Tag.NAMED),
        ELSE("else", Tag.NAMED),
        LPAREN("("),
        RPAREN(")"),
        LCURLY("{"),
        RCURLY("}"),
        LSQU("["),
        RSQU("]"),
        LIST("list", Tag.NAMED),
        MAP("map", Tag.NAMED),
        LINK("link", Tag.NAMED),
        SET("set", Tag.NAMED),
        AND("and", Tag.NAMED),
        ANDOP("&&"),
        OROP("||"),
        NOTOP("!"),
        OR("or", Tag.NAMED),
        NOT("not", Tag.NAMED),
        GT(">"),
        LT("<"),
        GTE(">="),
        LTE("<="),
        NOTEQU("!="),
        EQUEQU("=="),
        EQU("="),
        BITOR("|"),
        BITAND("^"),
        LSHIFT("<<"),
        RSHIFT(">>"),
        TRSHIFT(">>>"),
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/"),
        FLOORDIV("//"),
        MOD("%"),
        POWER("**"),
        IS("is", Tag.NAMED),
        //		ISNOT("is not",Tag.NAMED),
        IN("in", Tag.NAMED),
        LOOP("loop", Tag.NAMED),
        FOR("for", Tag.NAMED),
        SQOUTE("'"),
        DQOUTE("\""),
        DOT("."),
        IMPORT("@"),
        SINGLECOMMENT("#"),
        MULTICOMMENT("/* */"),
        THIS("this", Tag.NAMED),
        NULL("null", Tag.NAMED),
        CONTINUE("continue", Tag.NAMED),
        RET("return", Tag.NAMED),
        BREAK("break", Tag.NAMED),
        VARARGS("..."),
        RANGE(".."),
        TRUE("true", Tag.NAMED),
        FALSE("false", Tag.NAMED),
        ERROR,
        END("EOF");


        TokenKind() {
            this(null, Tag.DEFAULT);
        }

        TokenKind(String val) {
            this(val, Tag.DEFAULT);
        }

        TokenKind(Tag tag) {
            this(null, tag);
        }

        TokenKind(String val, Tag tag) {
            this.val = val;
            this.tag = tag;
        }

        final String val;
        final Tag tag;

        @Override
        public String toString() {
            return val;
        }
    }

    public static class Token {

        enum Tag {
            NAMED,
            NUMERIC,
            STRING,
            DEFAULT
        }

        public final TokenKind kind;
        public final Position pos;
        public final String comment;

        public Token(TokenKind kind, Position pos, String comment) {
            this.kind = kind;
            this.pos = pos;
            this.comment = comment;
        }

        public String value() {
            return kind.val;
        }

    }

    public final static class NamedToken extends Token {
        public String name;

        public NamedToken(TokenKind kind, String name, Position pos, String comments) {
            super(kind, pos, comments);
            this.name = name;
        }

        @Override
        public String value() {
            return name;
        }
    }

    public final static class StringToken extends Token {
        public String string;

        public StringToken(TokenKind kind, String string, Position pos, String comments) {
            super(kind, pos, comments);
            this.string = string;
        }

        @Override
        public String value() {
            return string;
        }
    }

    public final static class NumericToken extends Token {
        public String number;

        public NumericToken(TokenKind kind, String number, Position pos, String comments) {
            super(kind, pos, comments);
            this.number = number;
        }

        @Override
        public String value() {
            return number;
        }
    }
}
