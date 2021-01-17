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

package compiler.main.cli;

import compiler.main.Main;

import java.util.HashMap;
import java.util.Map;

public interface Command {
    Map<CommandName, Command> commands = new HashMap<>();

    enum CommandName {
        COMPILE("CompileCommand"),
        RUN("RunCommand");

        CommandName(String className) {
            this.commandClassName = className;
        }

        public String getCommandClassName() {
            return commandClassName;
        }

        String commandClassName;
    }

    Main.Result execute();

    static void registerCommand(CommandName type, Command command) {
        commands.put(type, command);
    }

    static Command getCommand(CommandName type) {
        return commands.get(type);
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

    default void callRun() {

    }
}
