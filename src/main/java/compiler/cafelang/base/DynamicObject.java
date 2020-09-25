package compiler.cafelang.base;

import java.util.HashMap;
import java.util.Map;

public class DynamicObject extends Obj{

    DynamicObject(){
        super(new ObjectPrototype());
    }

}
