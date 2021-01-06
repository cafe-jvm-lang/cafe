package compiler.main;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import compiler.main.cli.CLIArguments;
import compiler.main.cli.Command;

import java.util.Map;

/**
 * @author Dhyey
 * @version 0.0.1
 */
public final class Main {

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
//        Context context = new Context();
//        log = Log.instance(context);

        //arguments = CLIArguments.instance(context);
        //Command command = arguments.parse(args);

        JCommander cmd = new JCommander();
        cmd.setProgramName("cafe");

        Command.initCommands();
        for(Map.Entry<Command.CommandName, Command> command: Command.commands.entrySet()){
            cmd.addCommand(command.getValue());
        }

        try{
            cmd.parse(args);
            String command = cmd.getParsedCommand();
            JCommander parsedJCommander = cmd.getCommands().get(command);
            Object commandObject = parsedJCommander.getObjects().get(0);
            if(commandObject instanceof Command){
                ((Command) commandObject).execute();
            }
        }
        catch (ParameterException e){
            System.err.println(e.getMessage());
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Time elapsed: " + duration / 1000000 + "ms");

        return Result.OK;
    }
}
