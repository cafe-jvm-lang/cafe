/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

package compiler.main;

import com.beust.jcommander.DefaultUsageFormatter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import compiler.main.cli.Command;

import java.util.Map;

/**
 * @author Dhyey
 * @version 0.0.1
 */
public final class Main {
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

    static class GlobalArguments {
        @Parameter(names = {"--help"}, description = "Prints this message", help = true)
        boolean help;

        @Parameter(names = {"--usage"}, description = "Command name to print its usage")
        String usageCommand;
    }

    public Result compile(String[] args) {
        long startTime = System.nanoTime();

        GlobalArguments globalArguments = new GlobalArguments();

        JCommander cmd = new JCommander(globalArguments);
        DefaultUsageFormatter usageFormatter = new DefaultUsageFormatter(cmd);
        cmd.setProgramName("cafe");

        Command.initCommands();
        for(Map.Entry<Command.CommandName, Command> command: Command.commands.entrySet()){
            cmd.addCommand(command.getValue());
        }

        try{
            cmd.parse(args);
            if (globalArguments.usageCommand != null) {
                usageFormatter.usage(globalArguments.usageCommand);
            } else
            if(cmd.getParsedCommand() == null || globalArguments.help){
                cmd.usage();
            }else {
                String command = cmd.getParsedCommand();
                JCommander parsedJCommander = cmd.getCommands()
                                                 .get(command);
                Object commandObject = parsedJCommander.getObjects()
                                                       .get(0);
                if (commandObject instanceof Command) {
                    ((Command) commandObject).execute();
                } else {
                    return Result.CMDERR;
                }
            }
        }
        catch (ParameterException e){
            System.err.println(e.getMessage());
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("\nTime elapsed: " + duration / 1000000 + "ms");

        return Result.OK;
    }
}
