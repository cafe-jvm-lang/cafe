package cafelang.ir;

public class UnaryExpression extends ExpressionStatement<UnaryExpression>{
    private final OperatorType type;
    private ExpressionStatement<?> expressionStatement;

    private UnaryExpression(OperatorType type, ExpressionStatement<?> expressionStatement){
        this.type = type;
        this.expressionStatement = expressionStatement;
    }

    public static UnaryExpression create(Object type, Object expr){
        return new UnaryExpression(
                OperatorType.of(type),
                ExpressionStatement.of(expr)
        );
    }

    @Override
    protected UnaryExpression self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }
}
