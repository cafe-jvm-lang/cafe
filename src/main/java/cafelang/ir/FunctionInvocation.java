package cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class FunctionInvocation extends ExpressionStatement<FunctionInvocation> {
    private ReferenceLookup ref;
    private List<CafeElement<?>> arguments;

    private FunctionInvocation(ReferenceLookup ref, List<CafeElement<?>> arguments) {
        this.ref = ref;
        this.arguments = arguments;
    }

    public ReferenceLookup getReference() {
        return ref;
    }

    public static FunctionInvocation create(Object ref, List<Object> args) {
        List<CafeElement<?>> arguments = new LinkedList<>();
        for (Object arg : args) {
            if (arg instanceof CafeElement) {
                arguments.add((CafeElement) arg);
            } else {
                arguments.add(ExpressionStatement.of(arg));
            }
        }
        return new FunctionInvocation(
                ReferenceLookup.of(ref),
                arguments
        );
    }

    public int getArity() {
        return arguments.size();
    }

    public List<CafeElement<?>> getArguments() {
        return arguments;
    }

    @Override
    protected FunctionInvocation self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunctionInvocation(this);
    }
}
