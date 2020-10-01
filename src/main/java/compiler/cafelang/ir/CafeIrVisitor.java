package compiler.cafelang.ir;

public interface CafeIrVisitor {
    void visitModule(CafeModule module);
    void visitSymbolReference(SymbolReference symbolReference);

    void visitBlock(Block block);

    void visitFunction(CafeFunction cafeFunction);

    void visitAssignment(AssignmentStatement assignmentStatement);

    void visitBinaryExpression(BinaryExpression binaryExpression);

    void visitUnaryExpression(UnaryExpression unaryExpression);

    void visitConstantStatement(ConstantStatement constantStatement);

    void visitRefereceLookup(ReferenceLookup referenceLookup);

    void visitFunctionReference(FunctionReference functionReference);

    void visitDeclarativeAssignment(DeclarativeAssignmentStatement declarativeAssignmentStatement);

    void visitObjectAccess(ObjectAccessStatement objectAccessStatement);

    void visitMethodInvoke(MethodInvoke methodInvoke);

    void visitFunctionInvoke(FunctionInvoke functionInvoke);

    void visitNull(NullStatement aNull);
}
