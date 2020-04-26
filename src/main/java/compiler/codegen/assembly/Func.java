package compiler.codegen.assembly;

import java.lang.invoke.MethodHandle;

import org.objectweb.asm.Handle;

public interface Func {
	Func visitCode();
	<T> Func visitVarAsgn(String var,T value);
	Func visitFuncInvk(String name,String descriptor,Handle handle,Object... args);
	Func visitEnd();
	
	ExprFunc visitExpr(String funcName);
}
