package compiler.parser;

import java.util.List;

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
		switch(name) {
		case "if":
			return TokenKind.IF;
		case "else":
			return TokenKind.ELSE;
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
		case "is not":
			return TokenKind.ISNOT;
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
	 *
	 */
	public enum TokenKind {
		SEMICOLON(";"),
		COMMA(","),
		TILDE("~"),
		COLON(":"),
		NUMLIT(Tag.NUMERIC),
		STRLIT(Tag.STRING),
		IDENTIFIER(Tag.NAMED),
		IF("if",Tag.NAMED),
		ELSE("else",Tag.NAMED),
		LPAREN("("),
		RPAREN(")"),
		LCURLY("{"),
		RCURLY("}"),
		LSQU("["),
		RSQU("]"),
		LIST("list",Tag.NAMED),
		MAP("map",Tag.NAMED),
		LINK("link",Tag.NAMED),
		SET("set",Tag.NAMED),
		AND("and",Tag.NAMED),
		ANDOP("&&"),
		OROP("||"),
		NOTOP("!"),
		OR("or",Tag.NAMED),
		NOT("not",Tag.NAMED),
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
		IS("is",Tag.NAMED),
		ISNOT("is not",Tag.NAMED),
		IN("in",Tag.NAMED),
		LOOP("loop",Tag.NAMED),
		FOR("for",Tag.NAMED),
		SQOUTE("'"),
		DQOUTE("\""),
		DOT("."),
		IMPORT("@"),
		SINGLECOMMENT("#"),
		LCOMMENT("/*"),
		RCOMMENT("*/"),
		
		
		
		CONTINUE("continue",Tag.NAMED),
		RET("return",Tag.NAMED),
		BREAK("break",Tag.NAMED);
		
		
		TokenKind(){
			this(null,Tag.DEFAULT);
		}
		
		TokenKind(String val){
			this(val,Tag.DEFAULT);
		}
		
		TokenKind(Tag tag){
			this(null,tag);
		}
		
		TokenKind(String val,Tag tag){
			this.val = val;
			this.tag = tag;
		}
		
		final String val;
		final Tag tag;
	}

	public static class Token {
		
		enum Tag{
			NAMED,
			NUMERIC,
			STRING,
			DEFAULT
		}
		
		public final TokenKind kind;
		public final Position pos;
		public final List<String> comments;

		public Token(TokenKind kind, Position pos, List<String> comments) {
			this.kind = kind;
			this.pos = pos;
			this.comments = comments;
		}

	}
	
	public final static class NamedToken extends Token{
		public String name;
		
		public NamedToken(TokenKind kind, String name,Position pos, List<String> comments) {
			super(kind, pos, comments);
			this.name = name;
		}
	}
	
	public final static class StringToken extends Token{
		public String string;
		
		public StringToken(TokenKind kind, String string, Position pos, List<String> comments) {
			super(kind, pos, comments);
			this.string = string;
		}
	}
	
	public final static class NumericToken extends Token{
		public String number;
		
		public NumericToken(TokenKind kind, String number,Position pos, List<String> comments) {
			super(kind, pos, comments);
			this.number = number;
		}
	}
}
