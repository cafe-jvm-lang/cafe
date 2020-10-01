package compiler.gen;

import compiler.cafelang.ir.*;

public abstract class AbstractCafeIrVisitor implements CafeIrVisitor {
    @Override
    public void visitModule(CafeModule module) {
        module.walk(this);
    }

    @Override
    public void visitObjectAccess(ObjectAccessStatement objectAccessStatement) {

    }

    @Override
    public void visitMethodInvoke(MethodInvoke methodInvoke) {

    }

    @Override
    public void visitFunctionInvoke(FunctionInvoke functionInvoke) {

    }

    @Override
    public void visitNull(NullStatement aNull) {

    }

    @Override
    public void visitDeclarativeAssignment(DeclarativeAssignmentStatement declarativeAssignmentStatement) {

    }

    @Override
    public void visitFunctionReference(FunctionReference functionReference) {

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
    public void visitRefereceLookup(ReferenceLookup referenceLookup) {

    }
}
