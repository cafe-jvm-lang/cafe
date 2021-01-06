package compiler.main.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import compiler.main.CafeCompiler;
import compiler.main.Main;

@Parameters(commandNames = {"-c"}, commandDescriptionKey = "compile")
public class CompileCommand implements Command {

    @Parameter(descriptionKey = "source_file")
    String source;

    private CompileCommand(){
    }

    static {
        Command.registerCommand(CommandName.COMPILE, new CompileCommand());
    }

    @Override
    public Main.Result execute() {
        return new CafeCompiler(source).compile();
    }
}
