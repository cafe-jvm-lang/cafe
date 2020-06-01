package compiler.parser;

import compiler.parser.Tokens.Token;
import compiler.util.Position;

public interface Lexer {

	void nextToken();

	Token token();

	Token prevToken();

}
