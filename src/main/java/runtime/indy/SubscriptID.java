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

import library.Subscriptable;

import java.lang.invoke.*;

import static java.lang.invoke.MethodType.methodType;

public final class SubscriptID {
    private static final MethodHandle FALLBACK;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            FALLBACK = lookup.findStatic(
                    SubscriptID.class,
                    "fallback",
                    methodType(java.lang.Object.class, MethodCallSite.class, java.lang.Object[].class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error("Could not bootstrap the required method handles", e);
        }
    }

    static final class MethodCallSite extends MutableCallSite {
        final MethodHandles.Lookup callerLookup;
        String name;

        MethodCallSite(MethodHandles.Lookup caller, String name, MethodType type) {
            super(type);
            this.callerLookup = caller;
            this.name = name;
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) {
        MethodCallSite callSite = new MethodCallSite(caller, name, type);
        MethodHandle fallbackHandle = FALLBACK
                .bindTo(callSite)
                .asCollector(java.lang.Object[].class, type.parameterCount())
                .asType(type);
        callSite.setTarget(fallbackHandle);
        return callSite;
    }

    public static Object fallback(MethodCallSite callSite, java.lang.Object[] args) throws Throwable {
        if (args[0] instanceof Subscriptable) {
            Subscriptable obj = (Subscriptable) args[0];
            if (callSite.type()
                        .parameterCount() == 2) {
                return obj.getSubscript(args[1]);
            } else {
                obj.setSubscript(args[1], args[2]);
            }
            return null;
        }

        throw new IllegalAccessException("Type " + args[0].getClass() + " is not subscriptable");

    }
}
