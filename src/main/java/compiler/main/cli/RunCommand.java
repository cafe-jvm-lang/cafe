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
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import compiler.main.Main;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static compiler.util.Log.Type.MODULE_NOT_FOUND;
import static compiler.util.Messages.error;
import static compiler.util.Messages.message;
import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

@Parameters(commandNames = {"-r"}, separators = "=", commandDescription = "Executes compiled Cafe code")
public class RunCommand implements Command {

    @Parameter(names = {"-cp", "--classpath"},
            description = "A , separated list of directories to search for class files.")
    List<String> classPath = new LinkedList<>();

    @Parameter(descriptionKey = "arguments",
            description = "[runtime arguments]")
    List<String> arguments = new LinkedList<>();

    static {
        Command.registerCommand(CommandName.RUN, new RunCommand());
    }

    private RunCommand(){}

    public File getFile(String path){
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            return file;
        }
        throw new ParameterException("File doesn't exists or invalid file path provided");
    }

    public URLClassLoader getURLClassLoader(List<String> classpath) {
        if(classpath.isEmpty())
            classpath = Collections.singletonList(".");
        URL[] urls = new URL[classpath.size()];
        for (int i = 0; i < classpath.size(); i++) {
            try {
                urls[i] = new File(classpath.get(i)).toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return new URLClassLoader(urls);
    }

    public void run(Class<?> clazz, String[] arguments)throws Throwable{
        MethodHandle main;
        main = publicLookup().findStatic(clazz, "main", methodType(void.class, String[].class));
        main.invoke(arguments);
    }

    @Override
    public Main.Result execute() {
        URLClassLoader urlClassLoader = getURLClassLoader(classPath);
        try {
            Class<?> module = urlClassLoader.loadClass(arguments.get(0));
            run(module, arguments.subList(1, arguments.size()).toArray(new String[0]));
        }catch (ClassNotFoundException e){
            error(message(MODULE_NOT_FOUND, arguments.get(0)));
            return Main.Result.ERROR;
        }
        catch (Throwable t){
            t.printStackTrace();
            return Main.Result.ERROR;
        }

        return Main.Result.OK;
    }
}
