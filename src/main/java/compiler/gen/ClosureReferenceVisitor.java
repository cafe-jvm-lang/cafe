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

package compiler.gen;

import compiler.ir.*;

import java.util.*;

public class ClosureReferenceVisitor extends AbstractCafeIrVisitor {

    private static final class Context {
        final Deque<ReferenceTable> referenceTableStack = new LinkedList<>();
        final Set<String> accessedReferences = new HashSet<>();
        final List<String> closureReferences = new ArrayList<>();
    }

    private final Deque<Context> stack = new LinkedList<>();

    private void newContext() {
        stack.push(new Context());
    }

    private void dropContext() {
        stack.pop();
    }

    private Context context() {
        return stack.peek();
    }


    private void captureClosureReferences() {
        if (!stack.isEmpty()) {
            ReferenceTable table = context().referenceTableStack.peek();
            Set<String> currentBlockReferences = table.getOwnedReferences();
            context().accessedReferences.removeAll(currentBlockReferences);
            context().closureReferences.addAll(context().accessedReferences);

            for (String ref : context().accessedReferences) {
                table.add(SymbolReference.of(ref, SymbolReference.Kind.VAR, SymbolReference.Scope.LOCAL));
            }
        }
    }

    private void pushBlockTable(Block block) {
        if (!stack.isEmpty()) {
            context().referenceTableStack.push(block.getReferenceTable());
        }
    }

    private void dropBlockTable() {
        if (!stack.isEmpty())
            context().referenceTableStack.pop();
    }

    private void accessed(String name) {
        if (!stack.isEmpty()) {
            context().accessedReferences.add(name);
        }
    }

    @Override
    public void visitModule(CafeModule module) {
        module.walk(this);
    }

    @Override
    public void visitFunction(CafeFunction cafeFunction) {
        if (cafeFunction.isClosure()) {
            newContext();
            pushBlockTable(cafeFunction.getBlock());
            cafeFunction.walk(this);
            captureClosureReferences();
            dropBlockTable();
            cafeFunction.addClosureParameters(context().closureReferences);
            dropContext();
        } else {
            cafeFunction.walk(this);
        }
    }

    @Override
    public void visitBlock(Block block) {

        block.walk(this);


    }

    @Override
    public void visitFunctionInvocation(FunctionInvocation functionInvocation) {
        functionInvocation.getReference()
                          .accept(this);
        for (CafeElement<?> arg : functionInvocation.getArguments()) {
            arg.accept(this);
        }
    }

    @Override
    public void visitMethodInvocation(MethodInvocation methodInvocation) {
        for (CafeElement<?> arg : methodInvocation.getArguments()) {
            arg.accept(this);
        }
    }

    @Override
    public void visitReferenceLookup(ReferenceLookup referenceLookup) {
        accessed(referenceLookup.getName());
    }

    @Override
    public void visitDeclarativeAssignment(DeclarativeAssignmentStatement declarativeAssignmentStatement) {
        //declared(declarativeAssignmentStatement.getSymbolReference().getName());
    }
}
