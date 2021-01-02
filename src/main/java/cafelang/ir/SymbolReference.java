package cafelang.ir;

public class SymbolReference extends CafeElement<SymbolReference> {

    @Override
    protected SymbolReference self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitSymbolReference(this);
    }

    public enum Kind {
        VAR, CONST, GLOBAL_VAR, GLOBAL_CONST
    }

    private final String name;
    private final Kind kind;
    private int index = -1;

    private SymbolReference(String name, Kind kind) {
        this.kind = kind;
        this.name = name;
    }

    public static SymbolReference of(String name, Kind kind) {
        return new SymbolReference(name, kind);
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isGlobal() {
        return kind == Kind.GLOBAL_VAR || kind == Kind.GLOBAL_CONST;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "SymbolReference{" +
                "name='" + name + '\'' +
                ", kind=" + kind +
                ", index=" + index +
                '}';
    }
}
