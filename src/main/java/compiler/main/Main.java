package compiler.main;

import compiler.util.Context;

/**
 * @author Dhyey
 * @version 1.0
 */
public class Main {

	public enum Result {
		OK(0), // Compilation completed with no errors.
		ERROR(1); // Completed but reported errors.

		Result(int exitCode) {
			this.exitCode = exitCode;
		}

		public boolean isOK() {
			return (exitCode == 0);
		}

		public final int exitCode;
	}

	public Main() {
		Context context = new Context();

		Compiler c = Compiler.instance(context);
		c.compile();
	}

	public Result compile() {
		return null;
	}

}
