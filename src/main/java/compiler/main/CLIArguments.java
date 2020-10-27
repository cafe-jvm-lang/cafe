package compiler.main;

import java.io.File;

import compiler.util.Context;
import compiler.util.Log;
import compiler.util.LogType.Errors;
import compiler.util.Position;

/**
 * 
 * Arguments checking for cmd line args.
 * Status: Incomplete
 * @author Dhyey
 */
public class CLIArguments {
	public static final Context.Key<CLIArguments> argsKey = new Context.Key<>();

	private Log log;
	private File file;
	private SourceFileManager fileManager;
	
	public static CLIArguments instance(Context context) {
        CLIArguments instance = context.get(argsKey);
        if (instance == null) {
            instance = new CLIArguments(context);
        }
        return instance;
    }
	
	private CLIArguments(Context context) {
		context.put(argsKey, this);
		log = Log.instance(context);
		fileManager = SourceFileManager.instance(context);
	}
	
	
	public void checkArgs(String... args) {

		if(args.length <1) {
			log.error(Errors.NO_FILE_PATH_GIVEN_IN_CLI);
			log.printErrorLog();
			return;
		}

		fileManager.addSourceFile(args[0]);



		if(log.nerrors > 0)
			log.printErrorLog();
	}
}
