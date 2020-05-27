package compiler.main;

import compiler.util.Context;
import compiler.util.Log;

/**
 * @author Dhyey
 * @version 1.0
 */
public class Main {

	private Log log;

	public enum Result {
		OK(0), // Compilation completed with no errors.
		ERROR(1), // Completed but reported errors.
		CMDERR(2); // Bad cmd args

		Result(int exitCode) {
			this.exitCode = exitCode;
		}

		public boolean isOK() {
			return (exitCode == 0);
		}

		public final int exitCode;
	}

	public Main() {
	}

	public Result compile(String[] args) {
		Context context = new Context();
		log = Log.instance(context);

		Compiler c = Compiler.instance(context);
		c.compile();

		if (log.nerrors > 0 || log.nwarnings > 0) {
			log.printErrorLog();
			log.printWarningLog();
			return Result.ERROR;
		}

		return null;
	}

}
