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

package runtime;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

public final class ImportEvaluator {
    private static Map<URLPath, ExportMap> CURRENTLY_EXECUTING_MODULE_EXPORTS;
    private static Map<URLPath,ReferenceTable> CURRENT_MODULE_REF_TABLE = new HashMap<>();
    private static URLPath CURRENT_URL_PATH;
    private static Set<URLPath> EVAL = new HashSet<>();
    private static Set<URLPath> UNEVAL = new HashSet<>();
    private static Set<URLPath> EVALUATING = new HashSet<>();

    private static class CyclicDependencyException extends Exception{
        //String message = "Cyclic dependecy present in imports: %s and %s";
        CyclicDependencyException(String module){
            super(String.format("Cyclic dependecy present in imports for module %s",module));
        }
    }

    public static Map<URLPath, ExportMap> evalute(Class<?> module, URLPath modulePath) throws Throwable {
        Set<URLPath> imports = imports(module, modulePath);
        imports = updateSets(imports);
        imports = loadImportedModules(imports);

        Map<URLPath, ExportMap> exportMaps = new HashMap<>();
        for(URLPath importedModule: imports){
            if(UNEVAL.contains(importedModule)){
                if(EVALUATING.contains(importedModule)){
                    throw new CyclicDependencyException(importedModule.asString());
                }
                EVALUATING.add(importedModule);
                exportMaps.putAll(evalute(importedModule.getModule(), importedModule));
            }
        }
        CURRENTLY_EXECUTING_MODULE_EXPORTS = deepCopy(exportMaps);
        exportMaps.clear();
        CURRENT_URL_PATH = modulePath;
        exportMaps.put(modulePath,callGetExports(module));
        updateSets(modulePath);

        return exportMaps;
    }

    private static <K,V> Map<K,V> deepCopy(Map<K,V> map){
        Map<K, V> m = new HashMap<>();
        for(Map.Entry<K,V> entry: map.entrySet()){
            m.put(entry.getKey(), entry.getValue());
        }
        return m;
    }

    private static Set<URLPath> imports(Class<?> module, URLPath modulePath){
        ReferenceTable data;
        try {
            Method dataMethod = module.getMethod("#imports");
            data = (ReferenceTable) dataMethod.invoke(null, new Object[]{});
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            data = null;
        }
        Set<URLPath> paths = data.getURLPaths();
        CURRENT_MODULE_REF_TABLE.put(modulePath, data);
        return paths;
    }

    private static Set<URLPath> loadImportedModules(Set<URLPath> paths){
        for (URLPath path: paths) {
            try {
                String importPath = path.asString();
                String classpath = importPath+".class";
                File f = new File(classpath);
                classpath = f.getParentFile().getPath();
                String className = importPath.substring(importPath.lastIndexOf('/') + 1);
                URL url = new File(classpath).toURI().toURL();
                URLClassLoader loader = new URLClassLoader(new URL[]{url});
                Class<?> clazz = loader.loadClass(className);
                path.setModule(clazz);
            } catch (ClassNotFoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return paths;
    }

    private static Set<URLPath> updateSets(Set<URLPath> modules){
        UNEVAL.addAll(modules);
        UNEVAL.removeAll(EVAL);
        modules.retainAll(UNEVAL);
        return modules;
    }

    private static void updateSets(URLPath path){
        EVAL.add(path);
        UNEVAL.remove(path);
        EVALUATING.remove(path);
    }

    private static void callRun(Class<?> module) throws Throwable {
        MethodHandle main;
        main = publicLookup().findStatic(module, "#init", methodType(Map.class));
        main.invoke();
    }

    private static ExportMap callGetExports(Class<?> module) throws Throwable {
        MethodHandle main;
        main = publicLookup().findStatic(module, "#init", methodType(Map.class));
        Map<String, Object> exports = (Map<String, Object>) main.invoke();
        return ExportMap.of(module).with(exports);
    }

    public static Map<URLPath, ExportMap> getCurrentModuleExportMap(){
        return CURRENTLY_EXECUTING_MODULE_EXPORTS;
    }

    public static ReferenceTable getAliasNameTable(){
        return CURRENT_MODULE_REF_TABLE.get(CURRENT_URL_PATH);
    }
}
