package cafelang.runtime;

import cafelang.FunctionReference;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodType.genericMethodType;

public final class FunctionReferenceID {
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, String moduleClass, int arity, int varargs) throws Throwable {
        Class<?> module = caller.lookupClass().getClassLoader().loadClass(moduleClass);
        Method function = module.getDeclaredMethod(name, genericMethodType(arity, varargs == 1).parameterArray());
        function.setAccessible(true);
        return new ConstantCallSite(constant(
                FunctionReference.class,
                new FunctionReference(caller.unreflect(function))));
    }
}
