package compiler.cafelang.ir;

public interface CafeIrVisitor {
    void visitModule(CafeModule module);
    void visitSymbolReference(SymbolReference symbolReference);
}
