package cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class MethodInvocation extends ExpressionStatement<MethodInvocation>{
    private ExpressionStatement<?> invokedUpon;
    private List<CafeElement<?>> arguments;

    private MethodInvocation(ExpressionStatement<?> invokedUpon,List<CafeElement<?>> arguments){
        this.invokedUpon = invokedUpon;
        this.arguments = arguments;
    }

    public static MethodInvocation create(Object invokedOn,Object... args){
        List<CafeElement<?>> arguments = new LinkedList<>();
        for(Object arg: args){
            if(arg instanceof CafeElement){
                arguments.add((CafeElement) arg);
            }
            else{
                arguments.add(ExpressionStatement.of(arg));
            }
        }
        return new MethodInvocation(
                ExpressionStatement.of(invokedOn),
                arguments
        );
    }

    @Override
    protected MethodInvocation self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitMethodInvocation(this);
    }
}
