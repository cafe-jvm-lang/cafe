package compiler.parser;

import java.util.List;

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

	/**
	 * Add all tokens here
	 *
	 */
	public enum TokenKind {
		SEMICOLON(";");
		
		TokenKind(String val){
			this.val = val;
		}
		
		String val;
	}

	public static class Token {
		public final TokenKind kind;
		public final Position pos;
		public final List<String> comments;

		public Token(TokenKind kind, Position pos, List<String> comments) {
			this.kind = kind;
			this.pos = pos;
			this.comments = comments;
		}

	}
}
