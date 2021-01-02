package cafe;

public class DynamicObject extends BasePrototype {

    public DynamicObject() {
        super(new ObjectPrototype(), false);
    }

    public DynamicObject(BasePrototype __proto__){
        super(__proto__, false);
    }

    public static DynamicObject $_create(BasePrototype b, BasePrototype __proto__){
        return new DynamicObject(__proto__);
    }

    @Override
    public String toString() {
        return "<Dynamic Object>: \n ";
    }
}
