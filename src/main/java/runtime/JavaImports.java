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

import library.DObject;
import library.base.CFunc;
import library.base.CObject;
import library.io.BasicIO;
import runtime.imports.JavaModulePath;

import java.util.HashMap;
import java.util.Map;

import static runtime.LibraryDObjectGenerator.generate;

public final class JavaImports {
    private static Map<JavaModulePath, DObject> map = new HashMap<>();
    private static final Map<String, DObject> DEFAULT_IMPORTS;

    static {
        DEFAULT_IMPORTS = new HashMap<String, DObject>() {{
            put("Object", generate(CObject.class));
            put("Function", generate(CFunc.class));
            put("cmd", generate(BasicIO.class));
        }};
    }

    public static DObject getObject(JavaModulePath path) {
        return map.get(path);
    }

    public static DObject getDefaultImport(String name) {
        return DEFAULT_IMPORTS.get(name);
    }

    public static void add(JavaModulePath path, Class<?> module) {
        map.put(path, generate(module));
    }
}
