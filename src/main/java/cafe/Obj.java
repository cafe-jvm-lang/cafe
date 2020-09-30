package cafe;

import java.util.HashMap;
import java.util.Map;

public abstract class Obj {
    private final Map<String, Object> map = new HashMap<>();

    Obj(Object __proto__){
        map.put("__proto__",__proto__);
    }

    public void define(String key, Obj value){
        map.put(key,value);
    }

    public Object get(String key){
        return map.get(key);
    }
}
