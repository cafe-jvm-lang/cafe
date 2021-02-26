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
import library.base.CList;
import library.base.CObject;
import library.io.BasicIO;
import runtime.imports.JavaModulePath;

import java.util.HashMap;
import java.util.Map;

import static runtime.DObjectCreator.generateFrom;

public final class JavaImports {
    private static Map<JavaModulePath, DObject> map = new HashMap<>();
    private static final Map<String, DObject> DEFAULT_IMPORTS;
    private static final Map<JavaModulePath, String> DEFAULT_MODULE_PATHS;

    static {
        DEFAULT_IMPORTS = new HashMap<String, DObject>() {{
            put("Object", generateFrom(CObject.class));
            put("Function", generateFrom(CFunc.class));
            put("List", generateFrom(CList.class));
            put("cmd", generateFrom(BasicIO.class));
        }};

        DEFAULT_MODULE_PATHS = new HashMap<JavaModulePath, String>() {{
            put(new JavaModulePath("library.base.CObject", CObject.class), "Object");
            put(new JavaModulePath("library.base.CFunc", CFunc.class), "Function");
            put(new JavaModulePath("library.base.CList", CList.class), "List");
            put(new JavaModulePath("library.io.BasicIO", BasicIO.class), "cmd");
        }};
    }

    public static DObject getObject(JavaModulePath path) {
        DObject object = map.get(path);
        if (object == null) {
            String name = DEFAULT_MODULE_PATHS.get(path);
            object = DEFAULT_IMPORTS.get(name);
        }
        return object;
    }

    public static DObject getDefaultImport(String name) {
        return DEFAULT_IMPORTS.get(name);
    }

    public static void add(JavaModulePath path, Class<?> module) {
        if (DEFAULT_MODULE_PATHS.containsKey(path))
            return;
        map.put(path, generateFrom(module));
    }
}
