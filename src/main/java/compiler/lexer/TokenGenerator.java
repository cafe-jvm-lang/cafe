package compiler.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
			return TokenType.KW_VAR;
		} else if (keyword.equals("null")) {
			return TokenType.KW_NULL;
		} else if (keyword.equals("func")) {
			return TokenType.KW_FUNC;
		} else if (keyword.equals("if")) {
			return TokenType.KW_IF;
		} else if (keyword.equals("else")) {
			return TokenType.KW_ELSE;
		} else if (keyword.equals("and")) {
			return TokenType.KW_AND;
		} else if (keyword.equals("or")) {
			return TokenType.KW_OR;
		} else if (keyword.equals("not")) {
			return TokenType.KW_NOT;
		} else if (keyword.equals("xor")) {
			return TokenType.KW_XOR;
		} else if (keyword.equals("return")) {
			return TokenType.KW_RETURN;
		} else if (keyword.equals("loop")) {
			return TokenType.KW_LOOP;
		} else if (keyword.equals("break")) {
			return TokenType.KW_BREAK;
		} else if (keyword.equals("continue")) {
			return TokenType.KW_CONTINUE;
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
					tokenList.add(
							new Token(TokenType.SEP_LPAREN, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == ')') {
					tokenList.add(
							new Token(TokenType.SEP_RPAREN, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '{') {
					tokenList.add(
							new Token(TokenType.SEP_LBRACE, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '}') {
					tokenList.add(
							new Token(TokenType.SEP_RBRACE, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == ';') {
					tokenList.add(
							new Token(TokenType.SEP_SEMI, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '}') {
					tokenList.add(
							new Token(TokenType.SEP_RBRACE, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '+') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '+') {
						temp += ch;
						colCount++;
						tokenList.add(
								new Token(TokenType.OP_INC, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(
								new Token(TokenType.OP_PLUS, temp, new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '-') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '-') {
						temp += ch;
						colCount++;
						tokenList.add(
								new Token(TokenType.OP_DEC, temp, new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(TokenType.OP_MINUS, temp,
								new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '/') {
					tokenList.add(
							new Token(TokenType.OP_DIVIDE, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '*') {
					tokenList.add(
							new Token(TokenType.OP_MULTIPLY, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '&') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '&') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(TokenType.OP_LOGAND, temp,
								new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(TokenType.OP_BITAND, temp,
								new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '|') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '|') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(TokenType.OP_LOGOR, temp,
								new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(TokenType.OP_BITOR, temp,
								new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '!') {
					tokenList.add(
							new Token(TokenType.OP_LOGNOT, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '^') {
					tokenList.add(
							new Token(TokenType.OP_BITXOR, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '~') {
					tokenList.add(new Token(TokenType.OP_BITCOMPLIMENT, Character.toString(ch),
							new Position(rowCount, colCount)));
					charac = reader.read();
				} else if (ch == '<') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '<') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(TokenType.OP_BITLEFTSHIFT, temp,
								new Position(rowCount, colCount)));
						charac = reader.read();
					} else if (ch == '=') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(TokenType.OP_RELLE, temp,
								new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(TokenType.OP_RELLT, Character.toString(ch),
								new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '>') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '>') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(TokenType.OP_BITRIGHTSHIFT, temp,
								new Position(rowCount, colCount)));
						charac = reader.read();
					} else if (ch == '=') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(TokenType.OP_RELGE, temp,
								new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(new Token(TokenType.OP_RELGT, temp,
								new Position(rowCount, colCount)));
					}
					temp = "";
				} else if (ch == '=') {
					temp += ch;
					ch = (char) (charac = reader.read());
					if (ch == '=') {
						temp += ch;
						colCount++;
						tokenList.add(new Token(TokenType.OP_RELEE, temp,
								new Position(rowCount, colCount)));
						charac = reader.read();
					} else {
						tokenList.add(
								new Token(TokenType.OP_ASG, temp, new Position(rowCount, colCount)));
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

					tokenList.add(new Token(TokenType.TK_NUMLITERAL, temp, new Position(rowCount, startColCount)));

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
						tokenList.add(new Token(TokenType.TK_IDENTIFIER, temp, new Position(rowCount, startColCount)));
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
