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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import compiler.main.CafeCompiler;
import compiler.main.CompilerResult;
import compiler.main.Main;

@Parameters(commandNames = {"-c"}, commandDescription = "Compiles Cafe source files")
public class CompileCommand implements Command {

    @Parameter(description = "[source-file].cafe", required = true)
    String source;

    private CompileCommand() {
    }

    static {
        Command.registerCommand(CommandName.COMPILE, new CompileCommand());
    }

    @Override
    public Main.Result execute() {
        CompilerResult result = new CafeCompiler(source).compile();

        if (result.isOk()) {
            result.writeByteCode();
        }

        return result.getResult();
    }


}
