package cafelang.ir;

public class ConstantStatement extends ExpressionStatement<ConstantStatement>{
    private Object value;

    public ConstantStatement(Object o) {
        this.value = o;
    }

    @Override
    protected ConstantStatement self() {
        return this;
    }

    public static ConstantStatement of(Object o){
        if(o instanceof ConstantStatement)
            return (ConstantStatement) o;

        if(!isLiteralValue(o)){
            throw new IllegalArgumentException("Not a constant value: " + o);
        }
        return new ConstantStatement(o);
    }

    public static boolean isLiteralValue(Object v) {
        return v == null
                || v instanceof String
                || v instanceof Character
                || v instanceof Number
                || v instanceof Boolean
                ;
    }

    public Object value(){
        return value;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitConstantStatement(this);
    }
}
