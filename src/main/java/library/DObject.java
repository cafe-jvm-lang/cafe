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

package library;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.invoke.MethodType.genericMethodType;
import static java.lang.invoke.MethodType.methodType;

/**
 * Represents a Cafe Object. It sits at the root of entire Cafe object hierarchy. It contains a map of key-value pairs and provides methods to insert/modify/retrieve these pairs.
 */
public class DObject {
    protected final Map<String, Object> map;

    public DObject(DObject __proto__) {
        map = new HashMap<>();
        define(__PROTO__, __proto__);
    }

    public void define(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public String toString() {
        return "Object{" + map + "}";
    }

//    Runtime support
//    =============================================================

    public static final String __PROTO__ = "__proto__";
    public static final MethodHandle DISPATCH_CALL;
    public static final MethodHandle DISPATCH_GET;
    public static final MethodHandle DISPATCH_SET;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            DISPATCH_CALL = lookup.findStatic(DObject.class, "dispatchCall",
                    methodType(Object.class, String.class, Object[].class));
            DISPATCH_GET = lookup.findStatic(DObject.class, "getObject",
                    methodType(Object.class, String.class, DObject.class));
            DISPATCH_SET = lookup.findStatic(DObject.class, "setObject",
                    methodType(Object.class, String.class, DObject.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            throw new Error("Could not bootstrap the required method handles");
        }
    }

    public MethodHandle invoker(String property, MethodType type) {
        switch (type.parameterCount()) {
            case 0:
                throw new IllegalArgumentException(
                        "A dynamic object invoker type needs at least 1 argument");
            case 1:
                return DISPATCH_GET.bindTo(property)
                                   .asType(genericMethodType(1));
            case 2:
                return DISPATCH_SET.bindTo(property)
                                   .asType(genericMethodType(2));
        }
        return null;
    }

    public MethodHandle dispatchCallHandle(String property, MethodType type) {
        return DISPATCH_CALL.bindTo(property)
                            .asCollector(Object[].class, type.parameterCount());
    }

    public static Object getObject(String property, DObject object) {
        while (object != null) {
            if (object.get(property) != null)
                return object.get(property);
            object = (DObject) object.get(__PROTO__);
        }
        return null;
    }

    public static Object setObject(String property, DObject object, Object arg) {
        object.define(property, arg);
        return null;
    }

    public static Object dispatchCall(String property, Object... args) throws Throwable {
        DObject obj = (DObject) args[0];
        Object o = getObject(property, obj);
        if (o instanceof DFunc)
            return ((DFunc) o).invoke(args);
        throw new NoSuchMethodError(obj.toString() + " has no such method " + property);
    }
}
