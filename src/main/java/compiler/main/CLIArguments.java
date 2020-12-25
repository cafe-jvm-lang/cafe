package compiler.main;

import java.io.File;

import compiler.util.Context;
import compiler.util.Log;

import static compiler.util.Log.Type.NO_FILE_PATH_GIVEN_IN_CLI;
import static compiler.util.Messages.message;

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
			log.report(NO_FILE_PATH_GIVEN_IN_CLI, null,
					message(NO_FILE_PATH_GIVEN_IN_CLI));
			log.printIssues();
			return;
		}

		fileManager.addSourceFile(args[0]);

		if(log.entries() > 0)
			log.printIssues();
	}
}
