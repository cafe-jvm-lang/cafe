package compiler.cafelang.ir;

public abstract class ExpressionStatement<T extends ExpressionStatement<T>> extends CafeStatement<T> {
    public static ExpressionStatement<?> of(Object expr){
        if(expr instanceof ExpressionStatement)
            return (ExpressionStatement<?>) expr;

    }
}
