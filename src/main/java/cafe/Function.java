package cafe;

import java.lang.invoke.MethodHandle;

public class Function extends BasePrototype {
    private final MethodHandle handle;

    public Function(MethodHandle handle){
        super(new FunctionPrototype());
        this.handle = handle;
    }

    public MethodHandle handle(){
        return handle;
    }
}
