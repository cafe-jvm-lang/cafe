package cafelang.ir;

import java.util.Objects;

public class CafeImport extends CafeElement<CafeImport> {
    private final String functions;
    private final String moduleName;

    private CafeImport(String functions, String moduleName) {
        this.functions = functions;
        this.moduleName = moduleName;
    }

    public static CafeImport of(String functions, String moduleName) {
        return new CafeImport(functions, moduleName);
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    protected CafeImport self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitCafeImport(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeImport that = (CafeImport) o;
        return functions.equals(that.functions) &&
                moduleName.equals(that.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functions, moduleName);
    }
}
