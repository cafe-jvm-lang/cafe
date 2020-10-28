package compiler.gen;

import cafelang.ir.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class SymbolReferenceAssignmentVisitor extends AbstractCafeIrVisitor{

    private CafeModule module = null;
    private final AssignmentCounter assignmentCounter = new AssignmentCounter();
    private final Deque<ReferenceTable> tableStack = new LinkedList<>();

    private static class AssignmentCounter {

        // starts from 1 because 1st parameter is always DynamicObject
        private int counter = 1;

        public int next() {
            return counter++;
        }

        public void reset() {
            counter = 0;
        }
    }

    @Override
    public void visitModule(CafeModule module) {
        this.module = module;
        module.walk(this);
    }

    @Override
    public void visitDeclarativeAssignment(DeclarativeAssignmentStatement assignmentStatement) {
        SymbolReference reference = assignmentStatement.getSymbolReference();
        bindReference(reference);
        assignmentStatement.walk(this);
    }

    private void bindReference(SymbolReference reference) {
        ReferenceTable table = tableStack.peek();
        if (reference.getIndex() < 0) {
//            if (table.hasReferenceFor(reference.getName())) {
//                reference.setIndex(table.get(reference.getName()).getIndex());
//            }
            reference.setIndex(assignmentCounter.next());
        }
    }

    @Override
    public void visitFunction(CafeFunction cafeFunction) {
        assignmentCounter.reset();
        ReferenceTable table= cafeFunction.getBlock().getReferenceTable();
        for(String parameter: cafeFunction.getParameterNames()){
            SymbolReference ref = table.get(parameter);
            if(ref == null)
                // TODO: throw error
                ;
            else
                ref.setIndex(assignmentCounter.next());
        }

        cafeFunction.walk(this);
    }

    @Override
    public void visitObjectCreation(ObjectCreationStatement creationStatement) {
        if(creationStatement.index() < 0)
            creationStatement.setIndex(assignmentCounter.next());
        creationStatement.walk(this);
    }

    @Override
    public void visitFunctionInvocation(FunctionInvocation functionInvocation) {
        for(CafeElement<?> arg: functionInvocation.getArguments()){
            arg.accept(this);
        }
    }

    @Override
    public void visitBlock(Block block) {
        ReferenceTable table = block.getReferenceTable();
        tableStack.push(table);
        for(CafeStatement<?> statement : block.getStatements())
            statement.accept(this);
        tableStack.pop();
    }

    @Override
    public void visitReferenceLookup(ReferenceLookup referenceLookup) {

    }
}
