package compiler.cafelang.ir;

public class AssignmentStatement extends CafeStatement<AssignmentStatement>{
    private SymbolReference symbolReference;
    private boolean declaring = false;
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
        this.expressionStatement = ExpressionStatement.of(expr);
        return this;
    }

    @Override
    protected AssignmentStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitAssignment(this);
    }
}
