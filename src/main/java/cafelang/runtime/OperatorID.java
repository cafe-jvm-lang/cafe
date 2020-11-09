package cafelang.runtime;

import java.lang.invoke.*;

import static java.lang.invoke.MethodType.methodType;

public final class OperatorID {
    private static final MethodHandle FALLBACK_1;
    private static final MethodHandle FALLBACK_2;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            FALLBACK_1 = lookup.findStatic(
                    OperatorID.class,
                    "fallback_1",
                    methodType(Object.class,OperatorCallSite.class, Object[].class));

            FALLBACK_2 = lookup.findStatic(
                    OperatorID.class,
                    "fallback_2",
                    methodType(Object.class,OperatorCallSite.class, Object[].class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error("Could not bootstrap the required method handles", e);
        }
    }

    static class OperatorCallSite extends MutableCallSite{
        final MethodHandles.Lookup callerLookup;
        final String name;

        // Not supported yet
        MethodHandle fallback;

        OperatorCallSite(MethodHandles.Lookup callerLookup, String name, MethodType type) {
            super(type);
            this.callerLookup = callerLookup;
            this.name = name;
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, int arity) throws NoSuchMethodException, IllegalAccessException {
        OperatorCallSite callSite = new OperatorCallSite(caller, name, type);
        MethodHandle fallback;
        if (arity == 2) {
            fallback = FALLBACK_2;
        } else {
            fallback = FALLBACK_1;
        }

        MethodHandle fallbackHandle = fallback
                .bindTo(callSite)
                .asCollector(Object[].class, type.parameterCount())
                .asType(type);

        callSite.setTarget(fallbackHandle);
        return callSite;
    }

    public static Object fallback_1(OperatorCallSite callSite, Object[] args) throws Throwable {
        return null;
    }

    public static Object fallback_2(OperatorCallSite callSite,Object[] args) throws Throwable {
        Class<?> arg1Class = (args[0] == null) ? Object.class : args[0].getClass();
        Class<?> arg2Class = (args[1] == null) ? Object.class : args[1].getClass();
        MethodHandle target;

        target = callSite.callerLookup.findStatic(
                OperatorID.class, callSite.name, methodType(Object.class, arg1Class, arg2Class));

        return target.invokeWithArguments(args);
    }
}
