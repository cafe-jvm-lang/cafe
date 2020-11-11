package cafelang.ir;

import java.util.Collections;
import java.util.List;

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

    public OperatorType getType() {
        return type;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Collections.singletonList(expressionStatement);
    }

    @Override
    protected UnaryExpression self() {
        return this;
    }

    @Override
    public String toString() {
        return "UnaryExpression{" +
                type +
                expressionStatement +
                '}';
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }
}
