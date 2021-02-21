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

import library.DFunc;
import library.DList;
import library.DObject;
import library.base.CFuncProto;
import library.base.CListProto;
import library.base.CObjectProto;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * This class consists exclusively of static methods that creates new Dynamic Objects & their subtypes or generates a Dynamic Object from provided Class object, along with setting corresponding __proto__ property. All dynamic objects at runtime should be created using methods of this class.
 */
public class DObjectCreator {

    public final static DObject OBJECT_PROTO;
    public final static DObject FUNC_PROTO;
    public final static DObject LIST_PROTO;

    private final static Map<Class<?>, DObject> mapper;

    static {
        OBJECT_PROTO = generate(CObjectProto.class, null, null);
        FUNC_PROTO = generate(CFuncProto.class, null, null);
        FUNC_PROTO.define(DObject.__PROTO__, OBJECT_PROTO);
        setFuncProto(OBJECT_PROTO);
        setFuncProto(FUNC_PROTO);

        LIST_PROTO = generateFrom(CListProto.class);
        mapper = new HashMap<>();
    }

    private DObjectCreator() {
    }

    public static DObject create() {
        return new DObject(OBJECT_PROTO);
    }

    public static DFunc createFunc(MethodHandle mh) {
        return new DFunc(FUNC_PROTO, mh);
    }

    public static DList createList() {
        return new DList(LIST_PROTO);
    }

    public static DObject generateFrom(Class<?> clazz) {
        return generate(clazz, OBJECT_PROTO, FUNC_PROTO);
    }

    private static DObject generate(Class<?> clazz, DObject __proto__, DObject __proto__Func) {
        DObject object = new DObject(__proto__);
        Method[] methods = clazz.getDeclaredMethods();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())
                    //  TODO: should importable methods be static ?

                    && Modifier.isStatic(method.getModifiers())
            ) {
                try {
                    MethodHandle mh = lookup.unreflect(method);
                    object.define(method.getName(), new DFunc(__proto__Func, mh));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                try {
                    Object o = field.get(null);
                    object.define(field.getName(), o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        mapper.put(clazz, object);

        return object;
    }

    private static void setFuncProto(DObject object) {
        for (String key : object.keys()) {
            if (object.get(key) instanceof DFunc) {
                DFunc function = (DFunc) object.get(key);
                function.define(DObject.__PROTO__, FUNC_PROTO);
            }
        }
    }
}
