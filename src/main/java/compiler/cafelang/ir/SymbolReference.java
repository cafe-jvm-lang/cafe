package compiler.cafelang.ir;

public class SymbolReference extends CafeElement<SymbolReference>{

    @Override
    protected SymbolReference self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitSymbolReference(this);
    }

    public enum Kind{
        VAR, CONST, GLOBAL_VAR, GLOBAL_CONST
    }

    private final String name;
    private final Kind kind;

    private SymbolReference(String name, Kind kind){
        this.kind = kind;
        this.name = name;
    }

    private static SymbolReference of(String name, Kind kind){
        return new SymbolReference(name,kind);
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

}
