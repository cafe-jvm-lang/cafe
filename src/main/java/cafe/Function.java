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

    public Object invoke(Object... args) throws Throwable {
        return handle.invokeWithArguments(args);
    }

    @Override
    public String toString() {
        return super.toString("Function");
    }
}
