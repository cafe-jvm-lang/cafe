package compiler.parser;

import compiler.parser.Tokens.Token;

public interface Lexer {

    void nextToken();

    Token token();

    Token prevToken();

}
