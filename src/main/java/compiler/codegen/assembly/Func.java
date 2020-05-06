package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

import java.lang.invoke.MethodType;

import org.objectweb.asm.Handle;

import compiler.utils.HandleType;

public interface Func {
	
	Handle OPERATOR_HANDLE = makeHandle("OperatorInvocation","I");
	Handle FUNCTION_HANDLE = makeHandle("MethodInvocation", "[Ljava/lang/Object;");
	
	int FLAG_PRIVATE_STATIC = ACC_PRIVATE | ACC_STATIC;
	int FLAG_PUBLIC_STATIC = ACC_PUBLIC | ACC_STATIC;
	
	Func init();
	Func declareVar(String var);
	Func initVarAsgn(String var);
	Func initVarAsgnEnd();
	Func invokeFunc(String name, int args,HandleType handleType);
	Func loadLiteral(Object num);
	Func loadIdentifier(String idName);
	Func loadReturnValue();
	Func declareIfCondition();
	Func end();
	
	static String functionSignature(int x) {
		return MethodType.genericMethodType(x).toMethodDescriptorString();
	}
	
	static Handle makeHandle(String className, String description) {
	    return new Handle(H_INVOKESTATIC,
	      "compiler/codegen/runtime/" + className,
	      "bootstrap",
	      "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;"
	      + description + ")Ljava/lang/invoke/CallSite;", false);
	}
}
