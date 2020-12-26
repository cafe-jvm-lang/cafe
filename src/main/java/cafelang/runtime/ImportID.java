package cafelang.runtime;

import cafe.Function;

import java.lang.invoke.*;
import java.lang.reflect.Method;

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

        Object obj = Imports.searchFromImports(callerClass, callSite.name,-1);
        if(obj != null){
            if(obj instanceof Method){
                Method method = (Method) obj;
                MethodHandle handle = caller.unreflect(method);
                Function function = new Function(handle);
                return function;
            }
        }

        throw new NullPointerException("On Method:"+callSite.name);
    }
}
