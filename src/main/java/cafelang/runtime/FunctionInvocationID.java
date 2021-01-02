package cafelang.runtime;

import cafe.Function;

import java.lang.invoke.*;
import java.lang.reflect.Method;

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
//        for(int i=0;i<args.length;i++){
//            System.out.println(args[i]);
//        }

        MethodHandle target = null;
        MethodHandle invoker = null;

        // check in imports
        if (args[0] == null) {
            String name = callSite.name;
            MethodHandles.Lookup caller = callSite.callerLookup;
            Class<?> callerClass = caller.lookupClass();

            Object obj = Imports.searchFromImports(callerClass, name, args.length - 1);
            if (obj != null) {
                if (obj instanceof Method) {
                    Method method = (Method) obj;
                    target = caller.unreflect(method);
                }
            } else
                throw new NoSuchMethodError(name+callSite.type().toMethodDescriptorString());
        } else {
            Function targetRef = (Function) args[0];
            target = targetRef.handle();
        }

        invoker = MethodHandles.dropArguments(target, 0, Function.class);
        //System.out.println(invoker);
        return invoker.invokeWithArguments(args);
    }
}
