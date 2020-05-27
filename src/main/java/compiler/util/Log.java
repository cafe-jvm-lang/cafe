package compiler.util;

import java.util.ArrayList;
import java.util.List;

import compiler.util.LogType.Errors;
import compiler.util.LogType.Error;
import compiler.util.LogType.Warning;
import compiler.util.LogType.Warnings;

public class Log {
	public static final Context.Key<Log> logKey = new Context.Key<>();

	private final List<Error> errorList;
	private final List<Warning> warningList;

	public int nerrors = 0;
	public int nwarnings = 0;

	protected Log(Context context) {
		context.put(logKey, this);

		errorList = new ArrayList<>();
		warningList = new ArrayList<>();
	}

	public static Log instance(Context context) {
		Log instance = context.get(logKey);
		if (instance == null)
			instance = new Log(context);
		return instance;
	}

	public void error(Position pos, Errors err) {
		nerrors++;
		errorList.add(new Error(pos, err));
	}

	public void warning(Position pos, Warnings warn) {
		nwarnings++;
		warningList.add(new Warning(pos, warn));
	}

	public void printErrorLog() {
		if (nerrors > 0) {
			System.out.println("Total Errors: " + nerrors);
			errorList.stream().forEach(e -> System.err.println(e.toString()));
		}
	}

	public void printWarningLog() {
		if (nwarnings > 0) {
			System.err.println("Total Errors: " + nwarnings);
			warningList.stream().forEach(e -> System.err.println(e.toString()));
		}
	}
}
