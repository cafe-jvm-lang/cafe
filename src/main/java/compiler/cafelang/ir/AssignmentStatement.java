package compiler.cafelang.ir;

import java.util.Collections;
import java.util.List;

public class AssignmentStatement extends CafeStatement<AssignmentStatement>
                                 implements ReferencesHolder{

    private SymbolReference symbolReference;
    private boolean declaring = false;
    private boolean isAssigned = false;
    private ExpressionStatement<?> expressionStatement;

    public static AssignmentStatement create(SymbolReference ref, Object value, boolean declaring){
        return new AssignmentStatement().to(ref).as(value);
    }

    public AssignmentStatement to(SymbolReference ref){
        if(ref == null){
            throw new IllegalArgumentException("Must assign to a reference");
        }
        this.symbolReference = ref;
        return this;
    }

    public AssignmentStatement as(Object expr){
        if(expr == null) {
            this.expressionStatement = null;
            isAssigned = false;
        }
        else {
            this.expressionStatement = ExpressionStatement.of(expr);
            isAssigned = true;
        }
        return this;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public boolean isDeclaring() {
        return declaring;
    }

    public AssignmentStatement setDeclaring(boolean v){
        declaring = v;
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
    protected AssignmentStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitAssignment(this);
    }

    @Override
    public List<SymbolReference> getReferences() {
        return List.of(symbolReference);
    }
}
