package compiler.parser;

import java.util.List;

import compiler.ast.Node;
import compiler.lexer.Token;
import compiler.lexer.tokentypes.TokenType;

public class Parser {
	
	private List<Token> tokenL;
	private Token currT;
	private static int curr = 0;
	
	public Parser(List<Token> tokenL) {
		this.tokenL = tokenL;
	}
	
	public Token getNextToken() {
		if(curr < tokenL.size()) {
			return tokenL.get(curr++);
		}
		System.out.println("Token list empty");
		return null;
	}
	
	Node parse() {
		currT = getNextToken();
		
	
		
		return null;
	}
}
