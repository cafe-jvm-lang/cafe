package compiler.cafelang.ir;

public interface CafeIrVisitor {
    void visitModule(CafeModule module);
    void visitSymbolReference(SymbolReference symbolReference);

    void visitBlock(Block block);

    void visitFunction(CafeFunction cafeFunction);

    void visitAssignment(AssignmentStatement assignmentStatement);
}
