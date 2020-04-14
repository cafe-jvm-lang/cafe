package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class MainFunc implements Func{

	private final ClassWriter cw;
	private final MethodVisitor mv;
	
	protected MainFunc(final ClassWriter cw) {
		this.cw = cw;
		mv = this.cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
	}
	
	@Override
	public Func visitCode() {
		mv.visitCode();
		return this;
	}
	
	@Override
	public Func visitVarAsgn(String var, Object value) {
		
		return this;
	}

	@Override
	public Func visitFuncInvk() {

		return this;
	}

	@Override
	public Func visitEnd() {
		mv.visitInsn(RETURN);
		//	mv.visitMaxs(4,1);
			mv.visitEnd();
		return this;
	}

	@Override
	public ExprFunc visitExpr(String funcName) {
		return new ExprFunc(cw, funcName);
	}
}
