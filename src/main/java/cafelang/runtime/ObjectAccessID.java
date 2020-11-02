package cafelang.runtime;

import cafe.DynamicObject;

import java.lang.invoke.*;

import static java.lang.invoke.MethodType.methodType;

public final class MethodInvocationID {

    private static final MethodHandle FALLBACK;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            FALLBACK = lookup.findStatic(
                    MethodInvocationID.class,
                    "fallback",
                    methodType(Object.class, MethodCallSite.class, Object[].class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new Error("Could not bootstrap the required method handles", e);
        }
    }

    static final class MethodCallSite extends MutableCallSite{
        final MethodHandles.Lookup callerLookup;
        String name;

        MethodCallSite(MethodHandles.Lookup caller, String name, MethodType type){
            super(type);
            this.callerLookup = caller;
            this.name = name;
        }
    }

    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) {
        MethodCallSite callSite = new MethodCallSite(caller, name, type);
        MethodHandle fallbackHandle = FALLBACK
                .bindTo(callSite)
                .asCollector(Object[].class, type.parameterCount())
                .asType(type);
        callSite.setTarget(fallbackHandle);
        return callSite;
    }

    public static Object fallback(MethodCallSite callSite, Object[] args) throws Throwable {
        for (int i = 0; i < args.length; i++) {
            System.out.println("Arguments==>"+args[i]);
        }
        Class<?> clazz = args[0].getClass();
        MethodHandle target = lookupTarget(clazz,callSite,args);

        if(target == null)
            throw new NoSuchMethodError(clazz + "::" + callSite.name);
        return target.invokeWithArguments(args);
    }

    private static MethodHandle lookupTarget(Class<?> clazz, MethodCallSite callSite, Object[] args){
        for(Object arg : args)
            System.out.println(arg);
        if(args[0] instanceof DynamicObject){
            DynamicObject object = (DynamicObject) args[0];
            return object.invoker(callSite.name, callSite.type());
        }
        return null;
    }
}
