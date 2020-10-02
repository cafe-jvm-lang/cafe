package cafelang.ir;

import java.util.Collections;
import java.util.List;

public class FunctionWrapper extends ExpressionStatement<FunctionWrapper>{
    private CafeFunction target;

    private FunctionWrapper(CafeFunction function){
        this.target = function;
    }

    public static FunctionWrapper wrap(CafeFunction target){
        return new FunctionWrapper(target);
    }

    public CafeFunction getTarget() {
        return target;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Collections.singletonList(target);
    }

    @Override
    protected FunctionWrapper self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunctionWrapper(this);
    }
}
