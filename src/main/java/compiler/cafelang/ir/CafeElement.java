package compiler.cafelang.ir;

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

    public void walk(CafeIrVisitor visitor){
        for(CafeElement<?> e: children())
            e.accept(visitor);
    }
}
