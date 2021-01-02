package cafe;

public class FunctionPrototype extends BasePrototype {
    FunctionPrototype() {
        super(new ObjectPrototype(), false);
    }

    @Override
    public String toString() {
        return super.toString("FunctionPrototype");
    }
}
