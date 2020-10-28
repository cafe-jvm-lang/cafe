package cafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Map;

public class DynamicObject extends BasePrototype {

    public DynamicObject(){
        super(new ObjectPrototype());
    }

    public DynamicObject(Map<String, Object> map){
        super(new ObjectPrototype(),map);
    }

    public MethodHandle invoker(String property, MethodType type){
        return null;
    }

    public static final DynamicObject create(Map<String, Object> map){
        return new DynamicObject(map);
    }

    @Override
    public String toString() {
        return super.toString("DynamicObject");
    }
}
