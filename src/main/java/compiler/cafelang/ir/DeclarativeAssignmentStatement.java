package compiler.cafelang.ir;

import java.util.Collections;
import java.util.List;

public class DeclarativeAssignmentStatement extends ExpressionStatement<DeclarativeAssignmentStatement>{

    private SymbolReference symbolReference;
    private ExpressionStatement<?> expressionStatement;
    private boolean isAssigned=true;

    private DeclarativeAssignmentStatement() {
    }

    public static DeclarativeAssignmentStatement create(SymbolReference ref,Object expr){
        return new DeclarativeAssignmentStatement().to(ref).as(expr);
    }

    public DeclarativeAssignmentStatement to(SymbolReference ref){
        symbolReference = ref;
        return this;
    }

    public DeclarativeAssignmentStatement as(Object value){
        if(value == null){
            expressionStatement = new NullStatement();
            isAssigned = false;
        }
        else
            expressionStatement = ExpressionStatement.of(value);
        return this;
    }

    public SymbolReference getSymbolReference() {
        return symbolReference;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Collections.singletonList(expressionStatement);
    }

    @Override
    protected DeclarativeAssignmentStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitDeclarativeAssignment(this);
    }
}
