package cafe;

import java.util.HashMap;
import java.util.Map;

public abstract class BasePrototype {
    private final Map<String, Object> map;

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
}
