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
		errorList.add(new Error(pos, err));
	}

	public void warning(Position pos, Warnings warn) {
		warningList.add(new Warning(pos, warn));
	}

	public void printErrorLog() {
		int errorCount = errorList.size();
		if (errorCount > 0) {
			System.out.println("Total Errors: " + errorCount);
			errorList.stream().forEach(e -> System.out.println(e.toString()));
		}
	}

	public void printWarningLog() {
		int warnCount = warningList.size();
		if (warnCount > 0) {
			System.out.println("Total Errors: " + warnCount);
			warningList.stream().forEach(e -> System.out.println(e.toString()));
		}
	}
}
