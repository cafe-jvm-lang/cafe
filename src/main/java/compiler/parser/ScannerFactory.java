package compiler.parser;

import java.util.List;

import compiler.util.Context;
import compiler.util.Log;

public class ScannerFactory {
	public static final Context.Key<ScannerFactory> scannerFactoryKey = new Context.Key<>();

	final Log log;
	final Tokens tokens;

	private static Scanner scanner;

	static {
		try {
			Class.forName("compiler.parser.Scanner");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static ScannerFactory instance(Context context) {
		ScannerFactory instance = context.get(scannerFactoryKey);
		if (instance == null)
			instance = new ScannerFactory(context);
		return instance;
	}

	private ScannerFactory(Context context) {
		context.put(scannerFactoryKey, this);
		log = Log.instance(context);
		tokens = Tokens.instance(context);
	}

	static void registerScanner(Scanner sc) {
		scanner = sc;
	}

	public Lexer newScanner(List<Character> input) {
		return scanner.instance(this,input);
	}

}
