package compiler.cafelang.ir;

public class SubscriptStatement extends ExpressionStatement<SubscriptStatement>{
    private ExpressionStatement<?> subscriptOf;
    private ExpressionStatement<?> index;

    public SubscriptStatement(ExpressionStatement<?> subscriptOf, ExpressionStatement<?> index) {
        this.subscriptOf = subscriptOf;
        this.index = index;
    }

    public static SubscriptStatement create(Object subscriptOf,Object index){
        return new SubscriptStatement(
                ExpressionStatement.of(subscriptOf),
                ExpressionStatement.of(index)
        );
    }

    @Override
    protected SubscriptStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitSubscript(this);
    }
}
