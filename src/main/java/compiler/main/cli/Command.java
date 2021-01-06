package compiler.main.cli;

import compiler.main.Main;

import java.util.HashMap;
import java.util.Map;

public interface Command {
    Map<CommandName, Command> commands = new HashMap<>();

    enum CommandName {
        COMPILE("CompileCommand"),
        RUN("RunCommand");

        CommandName(String className){
            this.commandClassName = className;
        }

        public String getCommandClassName() {
            return commandClassName;
        }
        String commandClassName;
    }

    Main.Result execute();

    static void registerCommand(CommandName type, Command command){
        commands.put(type, command);
    }

    static void initCommands() {
        for (CommandName command : CommandName.values()) {
            try {
                Class.forName("compiler.main.cli." + command.getCommandClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
