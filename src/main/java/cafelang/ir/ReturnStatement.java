package cafelang.ir;

import java.util.Collections;
import java.util.List;

public class ReturnStatement extends CafeStatement<ReturnStatement>{
    private CafeStatement<?> expressionStatement;

    private ReturnStatement(ExpressionStatement<?> expression) {
        setExpressionStatement(expression);
    }

    public static ReturnStatement of(ExpressionStatement<?> value){
        return new ReturnStatement(value);
    }

    private void setExpressionStatement(CafeStatement<?> stat) {
        if (stat != null) {
            this.expressionStatement = stat;
        } else {
            this.expressionStatement = null;
        }
    }

    public CafeStatement<?> getExpressionStatement(){
        return expressionStatement;
    }

    @Override
    public List<CafeElement<?>> children() {
        if(expressionStatement != null)
            return Collections.singletonList(expressionStatement);
        return Collections.emptyList();
    }

    @Override
    protected ReturnStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitReturn(this);
    }
}
