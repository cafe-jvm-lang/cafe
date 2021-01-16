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

import java.lang.invoke.*;

import static java.lang.invoke.MethodType.methodType;

public final class FunctionInvocationID {
    private static final MethodHandle FALLBACK;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            FALLBACK = lookup.findStatic(
                    FunctionInvocationID.class,
                    "fallback",
                    methodType(Object.class, FunctionCallSite.class, Object[].class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error("Could not bootstrap the required method handles", e);
        }
    }

    public static class FunctionCallSite extends MutableCallSite {

        final MethodHandles.Lookup callerLookup;
        final String name;

        FunctionCallSite(MethodHandles.Lookup callerLookup, String name, MethodType type) {
            super(type);
            this.callerLookup = callerLookup;
            this.name = name;
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, Object... bsmArgs) {
        FunctionCallSite callSite = new FunctionCallSite(caller, name, type);
        MethodHandle fallbackHandle = FALLBACK
                .bindTo(callSite)
                .asCollector(Object[].class, type.parameterCount())
                .asType(type);
        callSite.setTarget(fallbackHandle);
        return callSite;
    }

    public static Object fallback(FunctionCallSite callSite, Object[] args) throws Throwable {
//        System.out.println("Name=>"+ callSite.name);
//        for(int i=0;i<args.length;i++){
//            System.out.println(args[i]);
//        }
//        System.out.println("====================");

        MethodHandle target = null;
        MethodHandle invoker = null;

        DFunc targetRef = (DFunc) args[0];
        target = targetRef.handle();

        invoker = MethodHandles.dropArguments(target, 0, DFunc.class);
        //System.out.println(invoker);
        return invoker.invokeWithArguments(args);
    }
}
