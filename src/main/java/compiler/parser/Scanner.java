package compiler.parser;

import compiler.parser.Tokens.Token;
import compiler.util.Position;

public class Scanner implements Lexer {

	static {
		ScannerFactory.registerScanner(new Scanner());
	}

	private Scanner() {

	}

	protected Scanner instance() {
		return new Scanner();
	}

	@Override
	public void nextToken() {

	}

	@Override
	public Token token() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Token prevToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position position() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void position(Position position) {
		// TODO Auto-generated method stub

	}

}
