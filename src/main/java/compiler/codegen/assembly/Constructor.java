package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

import compiler.utils.HandleType;

public class Constructor implements Func {
	private final ClassWriter cw;
	private final MethodVisitor mv;

	protected Constructor(final ClassWriter cw) {
		this.cw = cw;
		mv = this.cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
	}

	@Override
	public Func visitCode() {
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		return this;
	}

	@Override
	public Func loadIdentifier(String idName) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Func loadLiteral(Object num) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Func visitFuncInvk(String name, int args, HandleType handleType, Object... extraArgs) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Func visitVarAsgn(String var) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Func visitVarAsgnEnd() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Func visitEnd() {
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		return this;
	}

	@Override
	public ExprFunc visitExpr(String funcName) {
		// TODO Auto-generated method stub
		return null;
	}

//	public Constructor(ClassWriter cw) {
//		this.cw = cw;
//		
//		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
//		mv.visitCode();
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
//		
//	}

}
