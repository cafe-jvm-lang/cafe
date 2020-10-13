package cafelang.runtime;

import cafe.Function;

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
                    methodType(Object.class, Object[].class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error("Could not bootstrap the required method handles", e);
        }
    }
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, Object... bsmArgs) {
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle fallbackHandle = FALLBACK
                .asCollector(Object[].class, type.parameterCount())
                .asType(type);
        callSite.setTarget(fallbackHandle);
        return callSite;
    }

    public static Object fallback(Object[] args) throws Throwable{
        for(int i=0;i<args.length;i++){
            System.out.println(args[i]);
        }
        Function targetRef = (Function) args[0];
        MethodHandle target = targetRef.handle();
        MethodHandle invoker = MethodHandles.dropArguments(target, 0, Function.class);
        System.out.println(invoker);
        return invoker.invokeWithArguments(args);
    }
}
