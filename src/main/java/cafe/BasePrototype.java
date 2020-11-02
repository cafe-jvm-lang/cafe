package cafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.lang.invoke.MethodType.genericMethodType;
import static java.lang.invoke.MethodType.methodType;

public abstract class BasePrototype {
    private final Map<String, Object> map;
    private static final String __PROTO__ = "__proto__";

    public static final MethodHandle DISPATCH_CALL;
    public static final MethodHandle DISPATCH_GET;
    public static final MethodHandle DISPATCH_SET;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            DISPATCH_CALL = lookup.findStatic(DynamicObject.class, "dispatchCall", methodType(Object.class, String.class, Object[].class));
            DISPATCH_GET = lookup.findStatic(BasePrototype.class, "dispatchGetterStyle", methodType(Object.class, String.class, BasePrototype.class));
            DISPATCH_SET = lookup.findStatic(BasePrototype.class, "dispatchSetterStyle", methodType(Object.class, String.class, BasePrototype.class, Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            throw new Error("Could not bootstrap the required method handles");
        }
    }

    BasePrototype(Object __proto__){
        this(__proto__,new HashMap<>());
    }

    BasePrototype(Object __proto__, Map<String, Object> map){
        this.map = map;
        map.put("__proto__",__proto__);
    }

    public void define(String key, Object value){
        map.put(key,value);
    }

    public Object get(String key){
        return map.get(key);
    }

    public MethodHandle invoker(String property, MethodType type){
        switch (type.parameterCount()){
            case 0:
                throw new IllegalArgumentException("A dynamic object invoker type needs at least 1 argument (the receiver)");
            case 1:
                return DISPATCH_GET.bindTo(property).asType(genericMethodType(1));
            case 2:
                return DISPATCH_SET.bindTo(property).asType(genericMethodType(2));
        }
        return null;
    }

    public MethodHandle dispatchCallHandle(String property, MethodType type){
        return DISPATCH_CALL.bindTo(property).asCollector(Object[].class, type.parameterCount());
    }

    public static Object dispatchGetterStyle(String property, BasePrototype object) throws Throwable {
        while(object != null){
            if(object.get(property) != null)
                return object.get(property);
            object = (BasePrototype) object.get(__PROTO__);
        }
        return null;
    }

    public static Object dispatchSetterStyle(String property, BasePrototype object, Object arg) throws Throwable {
        object.define(property, arg);
        return null;
    }

    public static Object dispatchCall(String property, Object... args) throws Throwable {
        return null;
    }

    public String toString(String clazz){
        return clazz+'{' +
                 map +
                '}';
    }

    @Override
    public String toString() {
        return "BasePrototype{" +
                "map=" + map +
                '}';
    }
}
