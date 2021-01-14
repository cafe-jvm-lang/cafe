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

package cafelang.runtime;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

public final class ImportEvaluator {
    private static URLClassLoader classLoader;
    private static List<ExportMap> exportMaps = new LinkedList<>();
    private static Set<Class<?>> EVAL = new HashSet<>();
    private static Set<Class<?>> UNEVAL = new HashSet<>();
    private static Set<Class<?>> EVALUATING = new HashSet<>();

    private static class CyclicDependencyException extends Exception{
        //String message = "Cyclic dependecy present in imports: %s and %s";
        CyclicDependencyException(String module){
            super(String.format("Cyclic dependecy present in imports for module %s",module));
        }
    }

    public static List<ExportMap> evalute(Class<?> module, URLClassLoader loader) throws Throwable {
        classLoader = loader;
        List<Class<?>> imports = imports(module);
        updateSets(imports);

        for(Class<?> clazz: imports){
            if(UNEVAL.contains(clazz)){
                if(EVALUATING.contains(clazz)){
                    throw new CyclicDependencyException(clazz.getName());
                }
                EVALUATING.add(clazz);
                evalute(clazz, loader);
                callRun(clazz);
                exportMaps.add(callGetExports(clazz));
                updateSets(clazz);
            }
        }

        return exportMaps;
    }

    private static List<Class<?>> imports(Class<?> module){
        String[] imports = Imports.metadata("imports", module, new Class<?>[]{}, new Object[]{});
        List<Class<?>> importList = new LinkedList<>();
        for (int i = 0; i < imports.length; i++) {
            try {
                URL url = new File(imports[i]+".class").toURI().toURL();
                URLClassLoader loader = classLoader.newInstance(new URL[]{url});
                Class<?> clazz = loader.loadClass(imports[i]);
                importList.add(clazz);
            } catch (ClassNotFoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return importList;
    }

    private static void updateSets(List<Class<?>> modules){
        UNEVAL.addAll(modules);
        UNEVAL.removeAll(EVAL);
    }

    private static void updateSets(Class<?> clazz){
        EVAL.add(clazz);
        UNEVAL.remove(clazz);
        EVALUATING.remove(clazz);
    }

    private static void callRun(Class<?> module) throws Throwable {
        MethodHandle main;
        main = publicLookup().findStatic(module, "#init", methodType(Map.class));
        main.invoke();
    }

    private static ExportMap callGetExports(Class<?> module) throws Throwable {
        MethodHandle main;
        main = publicLookup().findStatic(module, "#exports", methodType(Map.class));
        Map<String, Object> exports = (Map<String, Object>) main.invoke();
        return ExportMap.of(module).with(exports);
    }
}
