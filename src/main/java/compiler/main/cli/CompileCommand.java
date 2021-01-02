package compiler.main.cli;

import compiler.main.Compiler;
import compiler.util.Context;

public class CompileCommand implements Command {
    private Context context;
    public CompileCommand(Context context){
        this.context = context;
    }

    @Override
    public void execute() {
        Compiler.instance(context).compile();
    }
}
