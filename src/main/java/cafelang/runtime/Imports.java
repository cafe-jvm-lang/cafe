package cafelang.runtime;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public final class Imports {

    private static final Class<?>[] EMPTY_TYPES = new Class<?>[]{};
    private static final Object[] EMPTY_ARGS = {};

    private Imports() {
        throw new UnsupportedOperationException("Don't instantiate utility classes");
    }

    static String[] metadata(String name, Class<?> callerClass, Class<?>[] types, Object[] args) {
        String[] data;
        try {
            Method dataMethod = callerClass.getMethod("$" + name, types);
            data = (String[]) dataMethod.invoke(null, args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // Cannot happen
            data = new String[]{};
        }
        return data;
    }

    public static String[] imports(Class<?> callerClass) {
        return metadata("imports", callerClass, EMPTY_TYPES, EMPTY_ARGS);
    }

    public static AccessibleObject searchFromImports(Class<?> callerClass, String functionName, int args) {
        String[] imports = imports(callerClass);
        AccessibleObject result = null;

        for (String imported : imports) {
            result = searchStaticMethod(
                    callerClass,
                    mergeImportAndFunction(imported, functionName),
                    args
            );

            if (result != null)
                return result;
        }

        return result;
    }

    private static String mergeImportAndFunction(String importName, String functionName) {
        return importName + "." + functionName;
    }

    public static AccessibleObject searchStaticMethod(Class<?> callerClass, String functionName, int args) {
        int funcClassSeparatorIndex = functionName.lastIndexOf(".");
        if (funcClassSeparatorIndex >= 0) {
            String className = functionName.substring(0, funcClassSeparatorIndex);
            String funcName = functionName.substring(funcClassSeparatorIndex + 1);

            try {
                Class<?> targetClass = Class.forName(className, true, callerClass.getClassLoader());
                return findStaticFunction(callerClass, targetClass, funcName, args);
            } catch (ClassNotFoundException ignored) {

            }
        }
        return null;
    }

    public static AccessibleObject findStaticFunction(Class<?> caller, Class<?> clazz, String name, int args) {
        Optional<Method> meth = Stream.of(clazz.getDeclaredMethods())
                                      .filter(m -> {
                                          if (!m.getName()
                                                .equals(name))
                                              return false;
                                          if (args != -1 && m.getParameterCount() != args)
                                              return false;
                                          return true;
                                      })
                                      .findFirst();
        if (meth.isPresent())
            return meth.get();
        return null;
    }
}
