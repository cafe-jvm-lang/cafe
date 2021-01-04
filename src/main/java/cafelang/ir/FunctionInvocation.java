package cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class FunctionInvocation extends ExpressionStatement<FunctionInvocation> {
    private String name;
    private ExpressionStatement<?> ref;
    private List<CafeElement<?>> arguments;

    private FunctionInvocation(String name, ExpressionStatement<?> ref, List<CafeElement<?>> arguments) {
        this.name = name;
        this.ref = ref;
        this.arguments = arguments;
    }

    public ExpressionStatement<?> getReference() {
        return ref;
    }

    public static FunctionInvocation create(Object ref, List<Object> args) {
        List<CafeElement<?>> arguments = new LinkedList<>();
        for (Object arg : args) {
            if (arg instanceof CafeElement<?>) {
                arguments.add((CafeElement<?>) arg);
            } else {
                arguments.add(ExpressionStatement.of(arg));
            }
        }
        String name = "#_ANN_CALL";
        if(ref instanceof ReferenceLookup)
            name= ((ReferenceLookup) ref).getName();
        return new FunctionInvocation(
                name,
                ExpressionStatement.of(ref),
                arguments
        );
    }

    public String getName() {
        return name;
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
