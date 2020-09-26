package compiler.cafelang.ir;

public class CafeModule extends CafeElement<CafeModule> {

    private String sourceFile;
    private final String clazz;
    private final ReferenceTable globalReferenceTable;

    public CafeModule(String clazz, ReferenceTable referenceTable) {
        this.clazz = clazz;
        this.globalReferenceTable = referenceTable;
    }

    public static CafeModule create(String clazz, ReferenceTable referenceTable){
        return new CafeModule(clazz,referenceTable);
    }

    @Override
    protected CafeModule self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitModule(this);
    }
}
