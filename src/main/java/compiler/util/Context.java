package compiler.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Context ensures that single context is used for each compiler phase invocation.
 * <p>Every phase registers itself with this context</p>
 *
 * @author Dhyey
 */
public class Context {
    public static class Key<T> {
    }

    protected final Map<Key<?>, Object> map = new HashMap<>();

    public <T> void put(Key<T> key, T fac) {
        Object old = map.put(key, fac);
        if (old != null) {
            throw new AssertionError("duplicate value");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        Object o = map.get(key);
        return (T) o;
    }
}
