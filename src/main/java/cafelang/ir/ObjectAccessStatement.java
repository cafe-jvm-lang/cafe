package cafelang.ir;

public class ObjectAccessStatement extends ExpressionStatement<ObjectAccessStatement>{
    private ExpressionStatement<?> accessedOn;
    private ExpressionStatement<?> property;

    public ObjectAccessStatement(ExpressionStatement<?> accessedOn, ExpressionStatement<?> property) {
        this.accessedOn = accessedOn;
        this.property = property;
    }

    public static ObjectAccessStatement create(Object accessedOn, Object property){
        return new ObjectAccessStatement(
                ExpressionStatement.of(accessedOn),
                ExpressionStatement.of(property)
        );
    }

    @Override
    protected ObjectAccessStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitObjectAccess(this);
    }
}
