package compiler.lexer;

public class Token {
	
	private TokenType type;
	private String value;
	private Position pos;
	
	public Token(TokenType type,String value, Position pos) {
		this.type = type;
		this.value = value;
		this.pos = pos;
	}
	
	public TokenType getTokenType() {
		return type;
	}
	
	public String getTokenValue() {
		return value;
	}
	
	public Position getPosition() {
		return pos;
	}
}
