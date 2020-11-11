package cafelang.ir;

import java.util.Arrays;
import java.util.List;

public class BinaryExpression extends ExpressionStatement<BinaryExpression>{
    private final OperatorType type;
    private ExpressionStatement<?> leftExpression;
    private  ExpressionStatement<?> rightExpression;

    private BinaryExpression(OperatorType type){
        this.type = type;
    }

    @Override
    protected BinaryExpression self() {
        return this;
    }

    public static BinaryExpression of(Object type){
        return new BinaryExpression(OperatorType.of(type));
    }

    public BinaryExpression left(Object expr){
        this.leftExpression = ExpressionStatement.of(expr);
        return this;
    }

    public BinaryExpression right(Object expr){
        this.rightExpression = ExpressionStatement.of(expr);
        return this;
    }

    public ExpressionStatement<?> right() {
        return rightExpression;
    }

    public ExpressionStatement<?> left() {
        return leftExpression;
    }

    public OperatorType getType() {
        return type;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Arrays.asList(leftExpression, rightExpression);
    }

    @Override
    public String toString() {
        return "BinaryExpression{" +
                "type=" + type +
                ", leftExpression=" + leftExpression +
                ", rightExpression=" + rightExpression +
                '}';
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }
}
