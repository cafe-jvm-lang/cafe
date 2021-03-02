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

import java.util.Deque;
import java.util.LinkedList;

public class SymbolReferenceAssignmentVisitor extends AbstractCafeIrVisitor {

    private final AssignmentCounter assignmentCounter = new AssignmentCounter();
    private final Deque<ReferenceTable> tableStack = new LinkedList<>();

    private static class AssignmentCounter {

        private int counter = 0;

        public int next() {
            return counter++;
        }

        public void set(int c) {
            counter = c;
        }

        public void reset() {
            counter = 0;
        }
    }

    @Override
    public void visitModule(CafeModule module) {
        module.walk(this);
    }

    @Override
    public void visitDeclarativeAssignment(DeclarativeAssignmentStatement assignmentStatement) {
        SymbolReference reference = assignmentStatement.getSymbolReference();
        if (!reference.isGlobal())
            bindReference(reference);
        assignmentStatement.walk(this);
    }

    private void bindReference(SymbolReference reference) {
        if (reference.getIndex() < 0) {
            reference.setIndex(assignmentCounter.next());
        }
    }

    @Override
    public void visitFunction(CafeFunction cafeFunction) {
        if (cafeFunction.isInit())
            assignmentCounter.set(0);
        else
            assignmentCounter.set(1);
        ReferenceTable table = cafeFunction.getBlock()
                                           .getReferenceTable();
        for (String parameter : cafeFunction.getParameterNames()) {
            SymbolReference ref = table.get(parameter);
            ref.setIndex(assignmentCounter.next());
        }

        cafeFunction.walk(this);
    }

    @Override
    public void visitObjectCreation(ObjectCreationStatement creationStatement) {
        if (creationStatement.index() < 0)
            creationStatement.setIndex(assignmentCounter.next());
        creationStatement.walk(this);
    }

    @Override
    public void visitListCollection(ListCollection listCollection) {
        if (listCollection.index() < 0)
            listCollection.setIndex(assignmentCounter.next());
        listCollection.walk(this);
    }

    @Override
    public void visitFunctionInvocation(FunctionInvocation functionInvocation) {
        for (CafeElement<?> arg : functionInvocation.getArguments()) {
            arg.accept(this);
        }
    }

    @Override
    public void visitMethodInvocation(MethodInvocation methodInvocation) {
        methodInvocation.getInvokedUpon()
                        .accept(this);
        for (CafeElement<?> arg : methodInvocation.getArguments()) {
            arg.accept(this);
        }
    }

    @Override
    public void visitBlock(Block block) {
        ReferenceTable table = block.getReferenceTable();
        tableStack.push(table);
        for (CafeStatement<?> statement : block.getStatements())
            statement.accept(this);
        tableStack.pop();
    }

    @Override
    public void visitForLoop(ForLoopStatement forLoopStatement) {
        if (forLoopStatement.getInitStatements() != null) {
            for (AssignedStatement init : forLoopStatement.getInitStatements())
                init.accept(this);
        }
        forLoopStatement.getBlock()
                        .accept(this);
    }
}
