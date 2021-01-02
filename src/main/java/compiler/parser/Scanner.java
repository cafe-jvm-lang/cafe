package compiler.parser;

import compiler.parser.Tokens.Token;

import java.util.List;

public class Scanner implements Lexer {

    static {
        ScannerFactory.registerScanner(new Scanner());
    }

    private Tokens tokens;
    private Token token;
    private Token prevToken;

    private Tokenizer tokenizer;

    private Scanner() {
    }

    private Scanner(ScannerFactory scannerFactory, List<Character> buff) {
        this.tokens = scannerFactory.tokens;
        token = prevToken = null;

        tokenizer = new Tokenizer(scannerFactory, buff);
    }

    protected Scanner instance(ScannerFactory scannerFactory, List<Character> input) {
        return new Scanner(scannerFactory, input);
    }

    @Override
    public void nextToken() {
        prevToken = token;
        token = tokenizer.readToken();
    }

    @Override
    public Token token() {
        return token;
    }

    @Override
    public Token prevToken() {
        return prevToken;
    }
}
