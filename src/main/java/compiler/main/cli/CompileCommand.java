package compiler.main.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import compiler.main.CafeCompiler;
import compiler.main.Main;

@Parameters(commandNames = {"-c"},  commandDescription = "Compiles Cafe source files")
public class CompileCommand implements Command {

    @Parameter(description = "[source-file].cafe", required = true)
    String source;

    private CompileCommand(){
    }

    static {
        Command.registerCommand(CommandName.COMPILE, new CompileCommand());
    }

    @Override
    public Main.Result execute() {
        if(source!=null)
            return new CafeCompiler(source).compile();
        return Main.Result.ERROR;
    }
}
