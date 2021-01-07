package cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class ObjectAccessStatement extends ExpressionStatement<ObjectAccessStatement> {
    private ExpressionStatement<?> accessedOn;
    private ExpressionStatement<?> property;

    public ObjectAccessStatement(ExpressionStatement<?> accessedOn, ExpressionStatement<?> property) {
        this.accessedOn = accessedOn;
        this.property = property;
    }

    public static ObjectAccessStatement create(Object accessedOn, Object property) {
        return new ObjectAccessStatement(
                ExpressionStatement.of(accessedOn),
                ExpressionStatement.of(property)
        );
    }

    public ExpressionStatement<?> getProperty() {
        return property;
    }

    public ExpressionStatement<?> getAccessedOn() {
        return accessedOn;
    }

    @Override
    public List<CafeElement<?>> children() {
        LinkedList<CafeElement<?>> list = new LinkedList<>();
        list.add(accessedOn);
        list.add(property);
        return list;
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
