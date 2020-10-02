package cafelang.ir;

public class PropertyAccess extends ExpressionStatement<PropertyAccess>{
    private String name;

    private PropertyAccess(String name){
        this.name = name;
    }

    public static PropertyAccess of(Object name){
        if(name instanceof String)
            return new PropertyAccess((String) name);
        throw cantConvert("String",name);
    }

    @Override
    protected PropertyAccess self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitPropertyAccess(this);
    }
}