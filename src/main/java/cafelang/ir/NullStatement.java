package cafelang.ir;

public class NullStatement extends ExpressionStatement<NullStatement> {
    @Override
    protected NullStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitNull(this);
    }
}
