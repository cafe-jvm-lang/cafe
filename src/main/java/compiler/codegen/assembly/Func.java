package compiler.codegen.assembly;

public interface Func {
	Func visitCode();
	<T> Func visitVarAsgn(String var,T value);
	Func visitFuncInvk();
	Func visitEnd();
	
	ExprFunc visitExpr(String funcName);
}
