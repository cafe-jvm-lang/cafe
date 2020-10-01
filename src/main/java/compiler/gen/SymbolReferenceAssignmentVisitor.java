package compiler.gen;

import compiler.cafelang.ir.*;

import java.util.Deque;
import java.util.LinkedList;

public class SymbolReferenceAssignmentVisitor extends AbstractCafeIrVisitor{

    private CafeModule module = null;
    private final AssignmentCounter assignmentCounter = new AssignmentCounter();
    private final Deque<ReferenceTable> tableStack = new LinkedList<>();

    private static class AssignmentCounter {

        private int counter = 0;

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
    }

    private void bindReference(SymbolReference reference) {
        ReferenceTable table = tableStack.peek();
        if (reference.getIndex() < 0) {
            if (table.hasReferenceFor(reference.getName())) {
                reference.setIndex(table.get(reference.getName()).getIndex());
            }
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
    public void visitBlock(Block block) {
        ReferenceTable table = block.getReferenceTable();
        tableStack.push(table);
        block.walk(this);
        tableStack.pop();
    }

    @Override
    public void visitRefereceLookup(ReferenceLookup referenceLookup) {

    }
}
