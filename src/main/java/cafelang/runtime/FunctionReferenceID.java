package cafelang.runtime;

import cafe.BasePrototype;
import cafe.Function;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodType.genericMethodType;

public final class FunctionReferenceID {
    public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type, String moduleClass, int arity, int varargs) throws Throwable {
        Class<?> module = caller.lookupClass()
                                .getClassLoader()
                                .loadClass(moduleClass);
        Method function = module.getDeclaredMethod(name, genericMethodType(arity, varargs == 1)
                .changeParameterType(0, BasePrototype.class)
                .parameterArray());
        function.setAccessible(true);
        return new ConstantCallSite(constant(
                Function.class,
                new Function(caller.unreflect(function))));
    }
}
