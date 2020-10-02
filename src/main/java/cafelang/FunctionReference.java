package cafelang;

import java.lang.invoke.MethodHandle;

public class FunctionReference {
    private final MethodHandle handle;

    public FunctionReference(MethodHandle handle){
        this.handle = handle;
    }

    public MethodHandle getHandle() {
        return handle;
    }
}
