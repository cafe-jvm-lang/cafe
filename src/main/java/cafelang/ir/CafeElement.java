package cafelang.ir;

import java.util.Collections;
import java.util.List;

public abstract class CafeElement<T extends CafeElement<T>> {
    private CafeElement<?> parent;
    protected abstract T self();

    private void setParent(CafeElement<?> parent) {
        this.parent = parent;
    }

    public final CafeElement<?> parent() {
        return this.parent;
    }

    public abstract void accept(CafeIrVisitor visitor);

    public List<CafeElement<?>> children(){
        return Collections.emptyList();
    }

    protected static final RuntimeException cantConvert(String expected, Object value) {
        return new ClassCastException(String.format(
                "expecting a %s but got a %s",
                expected,
                value == null ? "null value" : value.getClass().getName()));
    }

    public void walk(CafeIrVisitor visitor){
        for(CafeElement<?> e: children())
            e.accept(visitor);
    }
}
