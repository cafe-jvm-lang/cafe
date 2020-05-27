package compiler;

public class Main {

	/**
	 * Entry point into the compiler
	 * 
	 * @param args expects a file-name.txt file
	 */
	public static void main(String[] args) {
		System.exit(compile(args));
	}

	public static int compile(String[] args) {
		compiler.main.Main compiler = new compiler.main.Main();
		return compiler.compile(args).exitCode;
	}
}
