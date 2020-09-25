package compiler.cafelang.ir;

public class CafeModule extends CafeElement<CafeModule> {

    private String sourceFile;
    private final String Class;
    private final ReferenceTable globalReferenceTable;

    @Override
    protected CafeModule self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitModule(this);
    }
}
