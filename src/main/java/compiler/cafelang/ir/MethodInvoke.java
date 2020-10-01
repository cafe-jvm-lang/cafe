package compiler.cafelang.ir;

public class MethodInvoke extends ExpressionStatement<MethodInvoke>{
    @Override
    protected MethodInvoke self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitMethodInvoke(this);
    }
}
