package compiler.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenGenerator implements Lexer {

	private final List<Token> tokenList;
	private final List<String> keywordList;
	private Reader reader;

	public TokenGenerator(File f) {
		if (f.exists()) {
			tokenList = new ArrayList<Token>();
			reader = getBufferedReader(f);
			keywordList = getKeywordList();
		} else {
			tokenList = null;
			keywordList = null;

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

	List<String> getKeywordList() {
		return new ArrayList<String>(Arrays.asList("var", "null", "func", "if", "else", "and", "or", "not", "xor",
				"return", "loop", "break", "continue"));
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
					tokenList.add(new Token(TokenType.SEP_RBRACE, Character.toString(ch), new Position(rowCount, colCount)));
					charac = reader.read();
				}
				else if (Character.isDigit(ch)) {
					temp += ch;
					int startColCount = colCount;
					ch = (char) (charac = reader.read());
					
					while(Character.isDigit(ch)) {
						temp+=ch;
						colCount++;
						ch = (char) (charac = reader.read());
					}
					
					tokenList.add(new Token(TokenType.TK_NUMLITERAL,temp,new Position(rowCount,startColCount)));
					
					temp = "";
				}				
				else if (ch == '\n') {
					rowCount++;
					colCount=0;
					charac = reader.read();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Token getNextToken() {

		return null;
	}

}
