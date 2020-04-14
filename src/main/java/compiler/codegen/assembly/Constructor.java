package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class Constructor implements Func{
	private final ClassWriter cw;
	private final MethodVisitor mv;
	
	protected Constructor(final ClassWriter cw) {
		this.cw = cw;
		mv = this.cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
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
