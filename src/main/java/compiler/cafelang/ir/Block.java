package compiler.cafelang.ir;

public class Block extends ExpressionStatement<Block>{
    @Override
    protected Block self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitBlock(this);
    }
}
