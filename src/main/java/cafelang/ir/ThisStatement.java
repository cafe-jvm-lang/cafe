package cafelang.ir;

public class ThisStatement extends ExpressionStatement<ThisStatement> {

    private final boolean isGlobal;

    private ThisStatement(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public static ThisStatement create(boolean isGlobal) {
        return new ThisStatement(isGlobal);
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    protected ThisStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitThis(this);
    }
}
