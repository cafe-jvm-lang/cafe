package compiler.parser;

import java.util.List;

import compiler.parser.Tokens.Token;
import compiler.util.Log;

public class Tokenizer {

	private Log log;
	private Tokens tokens;
	private CharReader reader;
	
	protected Tokenizer(ScannerFactory scannerFactory, List<Character> buff) {
		this(scannerFactory, new CharReader(buff));
	}

	protected Tokenizer(ScannerFactory scannerFactory, CharReader reader) {
		log = scannerFactory.log;
		tokens = scannerFactory.tokens;
		this.reader = reader;
	}

	Token readToken() {
		return null;
	}

}
