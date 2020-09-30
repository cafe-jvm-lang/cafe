package compiler.cafelang.ir;

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

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }
}
