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
import library.DObject;
import library.base.CFuncProto;
import library.base.CObjectProto;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ProtoGenerator {
    private static DObject OBJECTPROTO;
    private static String objectproto = "CObjectProto";

    private static DObject FUNCPROTO;
    private static String funcproto = "CFuncProto";

    private ProtoGenerator() {
    }

    private static DObject generate(Class<?> clazz) {
        DObject object = new DObject();
        Method[] methods = clazz.getDeclaredMethods();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {
                try {
                    MethodHandle mh = lookup.unreflect(method);
                    object.define(method.getName(), (new DFunc(mh)));
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

        return object;
    }

    private static void setFuncProto(DObject object) {
        for (String key : object.keys()) {
            if (object.get(key) instanceof DFunc) {
                DFunc function = (DFunc) object.get(key);
                function.define(DObject.__PROTO__, FUNCPROTO);
            }
        }
    }

    private static Map<String, DObject> generate() {
        OBJECTPROTO = generate(CObjectProto.class);

        FUNCPROTO = generate(CFuncProto.class);
        FUNCPROTO.define(DObject.__PROTO__, OBJECTPROTO);

        setFuncProto(OBJECTPROTO);
        setFuncProto(FUNCPROTO);

        Map<String, DObject> map = new HashMap<>();
        map.put("CObjectProto", OBJECTPROTO);
        map.put("CFuncProto", FUNCPROTO);

        return map;
    }

    public static DObject getObjectProto() {
        if (OBJECTPROTO == null) {
            generate();
        }
        return OBJECTPROTO;
    }

    public static DObject getFuncProto() {
        if (FUNCPROTO == null) {
            generate();
        }
        return FUNCPROTO;
    }
}
