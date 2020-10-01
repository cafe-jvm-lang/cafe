package compiler.cafelang.ir;

public class FunctionReference extends ExpressionStatement<FunctionReference>{
    private CafeFunction target;

    FunctionReference(CafeFunction function){
        this.target = function;
    }

    @Override
    protected FunctionReference self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunctionReference(this);
    }
}
