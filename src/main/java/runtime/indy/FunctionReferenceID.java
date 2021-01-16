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

package runtime.indy;

import library.DFunc;
import library.DObject;
import runtime.DFuncCreator;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodType.genericMethodType;

public final class FunctionReferenceID {
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, String moduleClass, int arity, int varargs) throws Throwable {
        Class<?> module = caller.lookupClass()
                                .getClassLoader()
                                .loadClass(moduleClass);
        Method function = module.getDeclaredMethod(name, genericMethodType(arity, varargs == 1)
                .changeParameterType(0, DObject.class)
                .parameterArray());
        function.setAccessible(true);
        return new ConstantCallSite(constant(
                DFunc.class,
                DFuncCreator.create(caller.unreflect(function))));
    }
}
