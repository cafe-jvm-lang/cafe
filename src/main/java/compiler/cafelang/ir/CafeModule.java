package compiler.cafelang.ir;

public class CafeModule extends CafeElement<CafeModule> {


    @Override
    protected CafeModule self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitModule(this);
    }
}
