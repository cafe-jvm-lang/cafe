package cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class AssignmentStatement extends CafeStatement<AssignmentStatement> {

    private ExpressionStatement<?> lhsExpression;
    private ExpressionStatement<?> rhsExpression;

    public static AssignmentStatement create(Object ref, Object value){
        return new AssignmentStatement().to(ref).as(value);
    }

    public AssignmentStatement to(Object ref){
        if(ref == null){
            throw new IllegalArgumentException("Must assign to a reference");
        }
        this.lhsExpression = ExpressionStatement.of(ref);
        return this;
    }

    public AssignmentStatement as(Object expr) {

        this.rhsExpression = ExpressionStatement.of(expr);

        return this;
    }

    public ExpressionStatement<?> getLhsExpression() {
        return lhsExpression;
    }

    public ExpressionStatement<?> getRhsExpression(){
        return rhsExpression;
    }
    @Override
    public List<CafeElement<?>> children() {
        LinkedList<CafeElement<?>> children = new LinkedList<>();
        children.add(lhsExpression);
        children.add(rhsExpression);
        return children;
    }

    @Override
    protected AssignmentStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitAssignment(this);
    }

//    @Override
//    public List<SymbolReference> getReferences() {
//        return List.of(symbolReference);
//    }
}
