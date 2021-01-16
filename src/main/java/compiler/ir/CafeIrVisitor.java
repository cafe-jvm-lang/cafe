/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
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

package compiler.ir;

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

    void visitConditionalBranching(ConditionalBranching conditionalBranching);

    void visitForLoop(ForLoopStatement forLoopStatement);

    void visitCafeImport(CafeImport cafeImport);

    void visitBreakContinue(BreakContinueStatement breakContinueStatement);

    void visitCafeExport(CafeExport cafeExport);
}
