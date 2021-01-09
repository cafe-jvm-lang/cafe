/*
 * Copyright (c) 2021. Dhyey Shah <dhyeyshah4@gmail.com> 
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

package compiler.gen;

import cafelang.ir.*;

public abstract class AbstractCafeIrVisitor implements CafeIrVisitor {
    @Override
    public void visitSubscript(SubscriptStatement subscriptStatement) {
        subscriptStatement.walk(this);
    }

    @Override
    public void visitCafeImport(CafeImport cafeImport) {

    }

    @Override
    public void visitPropertyAccess(PropertyAccess propertyAccess) {

    }

    @Override
    public void visitReturn(ReturnStatement returnStatement) {
        returnStatement.walk(this);
    }

    @Override
    public void visitThis(ThisStatement thisStatement) {

    }

    @Override
    public void visitObjectCreation(ObjectCreationStatement creationStatement) {
        creationStatement.walk(this);
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
    public void visitConditionalBranching(ConditionalBranching conditionalBranching) {
        conditionalBranching.walk(this);
    }

    @Override
    public void visitConstantStatement(ConstantStatement constantStatement) {
        constantStatement.walk(this);
    }

    @Override
    public void visitForLoop(ForLoopStatement forLoopStatement) {

    }

    @Override
    public void visitReferenceLookup(ReferenceLookup referenceLookup) {
        referenceLookup.walk(this);
    }

    @Override
    public void visitBreakContinue(BreakContinueStatement breakContinueStatement) {

    }
}
