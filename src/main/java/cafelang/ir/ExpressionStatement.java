package cafelang.ir;

public abstract class ExpressionStatement<T extends ExpressionStatement<T>> extends CafeStatement<T> {
    public static ExpressionStatement<?> of(Object expr){
        if(expr == null)
            return null;
        if(expr instanceof ExpressionStatement)
            return (ExpressionStatement<?>) expr;

        throw cantConvert("ExpressionStatement",expr);
    }
}
