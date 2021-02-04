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

import compiler.main.CafeCompiler;
import compiler.main.CompilerResult;
import runtime.imports.CafeModulePath;

import java.util.*;
import java.util.stream.Collectors;

public class CafeClassLoader extends ClassLoader {

    // a map of <Set of unique Module Names> and there corresponding classloaders.
    private final static Map<Set<CafeModulePath>, CafeURLClassLoader> classLoaders = new HashMap<>();

    // a set unique module names, can be used as key for `classLoaders`.
    private final static List<Set<CafeModulePath>> uniqueModulesPaths = new ArrayList<>();

    public CafeClassLoader(ClassLoader loader) {
        super(loader);
    }

    public CafeClassLoader() {

    }

    private CafeURLClassLoader addModule(CafeModulePath path) {

        // check if module name is unique and can be added to any existing sets.
        for (Set<CafeModulePath> set : uniqueModulesPaths) {
            Set<String> set1 = set.stream()
                                  .map(e -> e.getClassName())
                                  .collect(Collectors.toSet());
            if (!set1.contains(path.getClassName())) {
                CafeURLClassLoader cl = classLoaders.get(set);
                cl.addModule(path);
                set.add(path);
                return cl;
            }
        }
        // If module name is not unique:
        // 1. create new set & add path to it.
        // 2. create new CafeURLClassLoader & add module to it.
        // 3. Update `classLoaders` map with newly created Set & CafeURLClassloader.
        // 4. Update `uniqueModulesPaths` with new Set.

        Set<CafeModulePath> newPaths = new HashSet<>();
        newPaths.add(path);
        CafeURLClassLoader cl = CafeURLClassLoader.with(path);
        cl.addModule(path);
        classLoaders.put(newPaths, cl);
        uniqueModulesPaths.add(newPaths);
        return cl;
    }

    public Class<?> loadModule(CafeModulePath path) throws ClassNotFoundException {
        for (Set<CafeModulePath> set : uniqueModulesPaths) {
            if (set.contains(path)) {
                CafeURLClassLoader cl = classLoaders.get(set);
                return cl.loadModule(path);
            }
        }

        return addModule(path).loadModule(path);
    }

    /**
     * Compiles & Loads JVM bytecode for a given Cafe source file.
     *
     * @param source cafe file to be compiled.
     * @return a class loaded from the compiled output.
     */
    public Class<?> compileAndLoadModule(String source) {
        CafeCompiler compiler = new CafeCompiler(source);
        CompilerResult result = compiler.compile();
        byte[] byteCode = result.getByteCode();
        Class<?> clazz = defineClass(null, byteCode, 0, byteCode.length);
        return clazz;
    }

    public void compileAndStoreModule(String source, String destination) {
        CafeCompiler compiler = new CafeCompiler(source);
        CompilerResult result = compiler.compile();
        if (destination == null || destination.isEmpty())
            result.writeByteCode();
        else
            result.writeByteCode(destination);
    }
}
