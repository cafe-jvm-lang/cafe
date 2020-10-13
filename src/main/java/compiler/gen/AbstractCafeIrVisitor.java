package compiler.gen;

import cafelang.ir.*;

public abstract class AbstractCafeIrVisitor implements CafeIrVisitor {
    @Override
    public void visitSubscript(SubscriptStatement subscriptStatement) {
        subscriptStatement.walk(this);
    }

    @Override
    public void visitPropertyAccess(PropertyAccess propertyAccess) {

    }

    @Override
    public void visitReturn(ReturnStatement returnStatement) {

    }

    @Override
    public void visitThis(ThisStatement thisStatement) {

    }

    @Override
    public void visitModule(CafeModule module) {
        module.walk(this);
    }

    @Override
    public void visitObjectAccess(ObjectAccessStatement objectAccessStatement) {
        objectAccessStatement.walk(this);
    }

    @Override
    public void visitMethodInvocation(MethodInvocation methodInvocation) {
        methodInvocation.walk(this);
    }

    @Override
    public void visitFunctionInvocation(FunctionInvocation functionInvocation) {

    }

    @Override
    public void visitNull(NullStatement aNull) {

    }

    @Override
    public void visitDeclarativeAssignment(DeclarativeAssignmentStatement declarativeAssignmentStatement) {
        declarativeAssignmentStatement.walk(this);
    }

    @Override
    public void visitFunctionWrapper(FunctionWrapper functionWrapper) {
        functionWrapper.walk(this);
    }

    @Override
    public void visitSymbolReference(SymbolReference symbolReference) {
        symbolReference.walk(this);
    }

    @Override
    public void visitBlock(Block block) {
        block.walk(this);
    }

    @Override
    public void visitFunction(CafeFunction cafeFunction) {
        cafeFunction.walk(this);
    }

    @Override
    public void visitAssignment(AssignmentStatement assignmentStatement) {
        assignmentStatement.walk(this);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.walk(this);
    }

    @Override
    public void visitUnaryExpression(UnaryExpression unaryExpression) {
        unaryExpression.walk(this);
    }

    @Override
    public void visitConstantStatement(ConstantStatement constantStatement) {
        constantStatement.walk(this);
    }

    @Override
    public void visitReferenceLookup(ReferenceLookup referenceLookup) {
        referenceLookup.walk(this);
    }
}
