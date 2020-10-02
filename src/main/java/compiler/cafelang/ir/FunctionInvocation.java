package compiler.cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class FunctionInvocation extends ExpressionStatement<FunctionInvocation>{
    private ReferenceLookup ref;
    private List<CafeElement<?>> arguments;

    private FunctionInvocation(ReferenceLookup ref, List<CafeElement<?>> arguments){
        this.ref = ref;
        this.arguments = arguments;
    }

    public static FunctionInvocation create(Object ref,Object... args){
        List<CafeElement<?>> arguments = new LinkedList<>();
        for(Object arg: args){
            if(arg instanceof CafeElement){
                arguments.add((CafeElement) arg);
            }
            else{
                arguments.add(ExpressionStatement.of(arg));
            }
        }
        return new FunctionInvocation(
                ReferenceLookup.of(ref),
                arguments
        );
    }

    @Override
    protected FunctionInvocation self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunctionInvoke(this);
    }
}
