package compiler.main;

import compiler.main.cli.CLIArguments;
import compiler.main.cli.Command;
import compiler.util.Context;
import compiler.util.Log;

/**
 * @author Dhyey
 * @version 1.0
 */
public class Main {

    private Log log;
    private CLIArguments arguments;

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
        long startTime = System.nanoTime();
        Context context = new Context();
        log = Log.instance(context);

        arguments = CLIArguments.instance(context);
        Command command = arguments.parse(args);

        if (log.entries() > 0)
            return Result.CMDERR;

        command.execute();

//        Compiler c = Compiler.instance(context);
//        c.compile();

        if (log.entries() > 0) {
            log.printIssues();
            return Result.ERROR;
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Time elapsed: " + duration / 1000000 + "ms");

        return Result.OK;
    }
}
