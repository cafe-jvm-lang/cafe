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

package cafelang.runtime;

import cafe.Function;

import java.lang.invoke.*;
import java.util.Map;

import static java.lang.invoke.MethodType.methodType;

public final class ImportID {
    private static final MethodHandle FALLBACK;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            FALLBACK = lookup.findStatic(
                    ImportID.class,
                    "fallback",
                    methodType(Object.class, ImportCallSite.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error("Could not bootstrap the required method handles", e);
        }
    }

    public static class ImportCallSite extends MutableCallSite {

        final MethodHandles.Lookup callerLookup;
        final String name;

        ImportCallSite(MethodHandles.Lookup callerLookup, String name, MethodType type) {
            super(type);
            this.callerLookup = callerLookup;
            this.name = name;
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) {
        ImportCallSite callSite = new ImportCallSite(
                caller,
                name,
                type
        );

        MethodHandle fallbackHandle = FALLBACK
                .bindTo(callSite)
                .asType(type);
        callSite.setTarget(fallbackHandle);
        return callSite;
    }

    public static Object fallback(ImportCallSite callSite) throws Throwable {
        MethodHandles.Lookup caller = callSite.callerLookup;
        Class<?> callerClass = caller.lookupClass();

        //Object obj = Imports.searchFromImports(callerClass, callSite.name, -1);
        Object obj = searchFromImports(callSite.name);
        if (obj != null) {
//            if (obj instanceof Method) {
//                Method method = (Method) obj;
//                MethodHandle handle = caller.unreflect(method);
//                Function function = new Function(handle);
//                return function;
//            }
            return obj;
        }

        throw new NoSuchMethodError(callSite.name+" "+callSite.type().toMethodDescriptorString());
    }

    public static Object searchFromImports(String name){
        ReferenceTable aliasImportTab = ImportEvaluator.getAliasNameTable();
        Map<URLPath, ExportMap> exports = ImportEvaluator.getCurrentModuleExportMap();

        ReferenceSymbol symbol = aliasImportTab.resolve(name);
        URLPath path = new URLPath(symbol.getPath());

        if(path == null){
            // TODO: default imports
                System.out.println(name);
                return new Function(null);

        }
        ExportMap export = exports.get(path);
        return export.get(symbol.getName());
    }
}
