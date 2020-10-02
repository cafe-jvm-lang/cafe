package cafelang.runtime;

import cafe.Function;
import cafelang.FunctionReference;

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
        FunctionReference targetRef = (FunctionReference) args[0];
        MethodHandle target = targetRef.getHandle();
        MethodHandle invoker = MethodHandles.dropArguments(target, 0, FunctionReference.class);
        MethodType type = invoker.type();
        System.out.println(args[1]);
        return invoker.invokeWithArguments(args);
    }
}
