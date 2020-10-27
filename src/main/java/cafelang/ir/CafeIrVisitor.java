package cafelang.ir;

public interface CafeIrVisitor {
    void visitModule(CafeModule module);
    void visitSymbolReference(SymbolReference symbolReference);

    void visitBlock(Block block);

    void visitFunction(CafeFunction cafeFunction);

    void visitAssignment(AssignmentStatement assignmentStatement);

    void visitBinaryExpression(BinaryExpression binaryExpression);

    void visitUnaryExpression(UnaryExpression unaryExpression);

    void visitConstantStatement(ConstantStatement constantStatement);

    void visitReferenceLookup(ReferenceLookup referenceLookup);

    void visitFunctionWrapper(FunctionWrapper functionWrapper);

    void visitDeclarativeAssignment(DeclarativeAssignmentStatement declarativeAssignmentStatement);

    void visitObjectAccess(ObjectAccessStatement objectAccessStatement);

    void visitMethodInvocation(MethodInvocation methodInvocation);

    void visitFunctionInvocation(FunctionInvocation functionInvocation);

    void visitNull(NullStatement aNull);

    void visitSubscript(SubscriptStatement subscriptStatement);

    void visitPropertyAccess(PropertyAccess propertyAccess);

    void visitReturn(ReturnStatement returnStatement);

    void visitThis(ThisStatement thisStatement);

    void visitObjectCreation(ObjectCreationStatement creationStatement);
}
