package compiler.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import compiler.lexer.tokentypes.KWTokenType;
import compiler.lexer.tokentypes.OpTokenType;
import compiler.lexer.tokentypes.SepTokenType;
import compiler.lexer.tokentypes.TokenType;

public class TokenGenerator implements Lexer {

	private final List<Token> tokenList;
	private Reader reader;

	public TokenGenerator(File f) {
		if (f.exists()) {
			tokenList = new ArrayList<Token>();
			reader = getBufferedReader(f);
		} else {
			tokenList = null;

			System.out.println("File Doesnt Exists");
		}
	}

	Reader getBufferedReader(File f) {
		try {
			return new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	TokenType checkKeywordTokenType(String keyword) {
		if (keyword.equals("var")) {
			return KWTokenType.KW_VAR;
		} else if (keyword.equals("null")) {
			return KWTokenType.KW_NULL;
		} else if (keyword.equals("func")) {
			return KWTokenType.KW_FUNC;
		} else if (keyword.equals("if")) {
			return KWTokenType.KW_IF;
		} else if (keyword.equals("else")) {
			return KWTokenType.KW_ELSE;
		} else if (keyword.equals("and")) {
			return KWTokenType.KW_AND;
		} else if (keyword.equals("or")) {
			return KWTokenType.KW_OR;
		} else if (keyword.equals("not")) {
			return KWTokenType.KW_NOT;
		} else if (keyword.equals("xor")) {
			return KWTokenType.KW_XOR;
		} else if (keyword.equals("return")) {
			return KWTokenType.KW_RETURN;
		} else if (keyword.equals("loop")) {
			return KWTokenType.KW_LOOP;
		} else if (keyword.equals("break")) {
			return KWTokenType.KW_BREAK;
		} else if (keyword.equals("continue")) {
			return KWTokenType.KW_CONTINUE;
		}
		return null;
	}

	@Override
	public List<Token> lex() {
		try {
			int charac = reader.read();
			int rowCount = 1, colCount = 0;
			String temp = "";

			while (charac != -1) {
				char ch = (char) charac;
				colCount++;
				if (ch == '(') {
					tokenList.add(new Token(SepTokenType.SEP_LPAREN, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == ')') {
					tokenList.add(new Token(SepTokenType.SEP_RPAREN, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '{') {
					tokenList.add(new Token(SepTokenType.SEP_LBRACE, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '}') {
					tokenList.add(new Token(SepTokenType.SEP_RBRACE, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == ';') {
					tokenList.add(
							new Token(SepTokenType.SEP_SEMI, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '}') {
					tokenList.add(new Token(SepTokenType.SEP_RBRACE, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '+') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '+') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_INC, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(OpTokenType.OP_PLUS, temp, new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '-') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '-') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_DEC, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(OpTokenType.OP_MINUS, temp, new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '/') {
					tokenList.add(
							new Token(OpTokenType.OP_DIVIDE, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '*') {
					tokenList.add(new Token(OpTokenType.OP_MULTIPLY, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '&') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '&') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_LOGAND, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(OpTokenType.OP_BITAND, temp, new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '|') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '|') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_LOGOR, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(OpTokenType.OP_BITOR, temp, new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '!') {
					tokenList.add(
							new Token(OpTokenType.OP_LOGNOT, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '^') {
					tokenList.add(
							new Token(OpTokenType.OP_BITXOR, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '~') {
					tokenList.add(new Token(OpTokenType.OP_BITCOMPLIMENT, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '<') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '<') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_BITLEFTSHIFT, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else if (ch == '=') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_RELLE, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(OpTokenType.OP_RELLT, Character.toString(ch),
								new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '>') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '>') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_BITRIGHTSHIFT, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else if (ch == '=') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_RELGE, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(OpTokenType.OP_RELGT, temp, new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '=') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '=') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(OpTokenType.OP_RELEE, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(OpTokenType.OP_ASG, temp, new Position(rowCount, colCount)));
					}
					temp = "";

				} else if (Character.isDigit(ch)) {
					temp += ch;
					int startColCount = colCount;
					ch = (char) (charac = reader.read());

					while (Character.isDigit(ch)) {
						temp += ch;
						colCount++;
						ch = (char) (charac = reader.read());
					}

					tokenList.add(new Token(KWTokenType.TK_NUMLITERAL, temp, new Position(rowCount, startColCount)));

					temp = "";
				} else if (Character.isLetter(ch)) {
					temp += ch;
					ch = (char) (charac = reader.read());
					int startColCount = colCount;
					int x;

					TokenType t;

					while (Character.isLetter(ch)) {
						temp += ch;
						colCount++;
						ch = (char) (charac = reader.read());
					}
					if ((t = checkKeywordTokenType(temp)) != null) {
						tokenList.add(new Token(t, temp, new Position(rowCount, startColCount)));
					} else {
						tokenList
								.add(new Token(KWTokenType.TK_IDENTIFIER, temp, new Position(rowCount, startColCount)));
					}
					temp = "";
				} else if (ch == '\n') {
					rowCount++;
					colCount = 0;
					charac = reader.read();
				} else {
					charac = reader.read();
				}
			}
			return tokenList;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
