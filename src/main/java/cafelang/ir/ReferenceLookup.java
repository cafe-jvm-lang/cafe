package cafelang.ir;

public class ReferenceLookup extends ExpressionStatement<ReferenceLookup>{

    private final String name;

    public ReferenceLookup(String name) {
        this.name = name;
    }

    public static ReferenceLookup of(Object name){
        if (name instanceof ReferenceLookup) {
            return (ReferenceLookup) name;
        }
        if (name instanceof SymbolReference) {
            return new ReferenceLookup(((SymbolReference) name).getName());
        }
        return new ReferenceLookup(name.toString());
    }

    public SymbolReference resolveIn(ReferenceTable table){
        return table.get(name);
    }

    @Override
    protected ReferenceLookup self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitReferenceLookup(this);
    }
}
