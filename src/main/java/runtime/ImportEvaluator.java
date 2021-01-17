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

import runtime.imports.ImportPathVisitor;
import runtime.imports.JavaModulePath;
import runtime.imports.ModulePath;
import runtime.imports.URLModulePath;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

public final class ImportEvaluator implements ImportPathVisitor {

    private static ModulePath CURR_MODULE_PATH = null;

    private static Map<ModulePath, ReferenceTable> IMPORT_TABLE = new HashMap<>();
    private static Map<ModulePath, ExportMap> EXPORT_TABLE = new HashMap<>();

    private static Set<ModulePath> EVAL = new HashSet<>();
    private static Set<ModulePath> UNEVAL = new HashSet<>();
    private static Set<ModulePath> EVALUATING = new HashSet<>();

    public void evaluate(Class<?> module) throws Throwable {
        Set<ModulePath> imports = imports(module);
        imports = updateSets(imports);

        ModulePath path = CURR_MODULE_PATH;
        loadImportedModules(imports);
        CURR_MODULE_PATH = path;

        EXPORT_TABLE.put(CURR_MODULE_PATH, runAndGetExports(module));
        reUpdateSets(CURR_MODULE_PATH);
    }

    private Set<ModulePath> imports(Class<?> module) {
        ReferenceTable data;
        try {
            Method dataMethod = module.getMethod("#imports");
            data = (ReferenceTable) dataMethod.invoke(null, new Object[]{});
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            data = null;
        }
        IMPORT_TABLE.put(CURR_MODULE_PATH, data);

        Set<ModulePath> paths = data.getImportPaths();
        return paths;
    }

    private static Set<ModulePath> updateSets(Set<ModulePath> modules) {
        UNEVAL.addAll(modules);
        UNEVAL.removeAll(EVAL);
        modules.retainAll(UNEVAL);
        EVALUATING.add(CURR_MODULE_PATH);
        return modules;
    }

    private void loadImportedModules(Set<ModulePath> paths) {
        for (ModulePath path : paths) {
            path.accept(this);
        }
    }

    @Override
    public void visit(URLModulePath path) {
        try {
            // cyclic imports
            if (EVALUATING.contains(path)) {
                throw new CyclicDependencyException(path.asString());
            }
            // already evaluated
            if (EVAL.contains(path)) {
                return;
            }

            String importPath = path.asString();
            String classpath = importPath + ".class";
            File f = new File(classpath);
            classpath = f.getParentFile()
                         .getPath();
            String className = importPath.substring(importPath.lastIndexOf('/') + 1);
            URL url = new File(classpath).toURI()
                                         .toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{url});
            Class<?> importedModule = loader.loadClass(className);

            EVALUATING.add(path);
            CURR_MODULE_PATH = path;
            evaluate(importedModule);

        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void visit(JavaModulePath path) {
        JavaImports.add(path, path.getModule());
    }

    private static ExportMap runAndGetExports(Class<?> module) throws Throwable {
        MethodHandle main;
        main = publicLookup().findStatic(module, "#init", methodType(Map.class));
        Map<String, Object> exports = (Map<String, Object>) main.invoke();
        return ExportMap.of(module)
                        .with(exports);
    }

    private static void reUpdateSets(ModulePath path) {
        EVAL.add(path);
        UNEVAL.remove(path);
        EVALUATING.remove(path);
    }

    public static ReferenceTable getImportTable() {
        return IMPORT_TABLE.get(CURR_MODULE_PATH);
    }

    public static Map<ModulePath, ExportMap> getExportTable() {
        return EXPORT_TABLE;
    }

    private static class CyclicDependencyException extends Exception {
        CyclicDependencyException(String module) {
            super(String.format("Cyclic dependecy present in imports for module %s", module));
        }
    }
}
