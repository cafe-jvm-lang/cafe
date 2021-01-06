package compiler.main.cli;

import compiler.main.SourceFileManager;
import compiler.util.Context;
import compiler.util.Log;

import java.io.File;

import static compiler.util.Log.Type.NO_FILE_PATH_GIVEN_IN_CLI;
import static compiler.util.Messages.message;

/**
 * Arguments checking for cmd line args.
 * Status: Incomplete
 *
 * @author Dhyey
 */
public class CLIArguments {
    public static final Context.Key<CLIArguments> argsKey = new Context.Key<>();

    private Log log;
    private File file;
    private SourceFileManager fileManager;
    private Context context;

    public static CLIArguments instance(Context context) {
        CLIArguments instance = context.get(argsKey);
        if (instance == null) {
            instance = new CLIArguments(context);
        }
        return instance;
    }

    private CLIArguments(Context context) {
        context.put(argsKey, this);
        this.context = context;
        log = Log.instance(context);
        fileManager = SourceFileManager.instance(context);
    }

    public Command parse(String... args) {

        if (args.length < 1) {
            log.report(NO_FILE_PATH_GIVEN_IN_CLI, null,
                    message(NO_FILE_PATH_GIVEN_IN_CLI));
            log.printIssues();
            return null;
        }

        return parseCommand(args[0]);
    }

    private Command parseCommand(String arg){
        int i = arg.lastIndexOf('.');
        if(i >= 0) {
            String type = arg.substring(i);
            if (type.equals(".cafe")) {
                return parseCompileCommand(arg);
            }
        }
        return null;
    }

    private CompileCommand parseCompileCommand(String fileName){
        fileManager.setSourceFile(fileName);
        return null;
    }

    private void parseRunCommand(){

    }
}
