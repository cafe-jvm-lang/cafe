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

import library.base.CFunc;
import library.base.CObject;

import java.util.HashMap;
import java.util.Map;

public final class JavaImports {
    private static Map<String, Object> map = new HashMap<>();

    static{
        // default imports
        map.put("Object", LibraryDObjectGenerator.generate(CObject.class));
        map.put("Function", LibraryDObjectGenerator.generate(CFunc.class));
    }

    public static Object getObject(String name){
        return map.get(name);
    }

    public static void add(String name, Object object){
        map.put(name, object);
    }
}
