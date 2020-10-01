package compiler.cafelang.ir;

public class FunctionInvoke extends ExpressionStatement<FunctionInvoke>{
    @Override
    protected FunctionInvoke self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunctionInvoke(this);
    }
}
