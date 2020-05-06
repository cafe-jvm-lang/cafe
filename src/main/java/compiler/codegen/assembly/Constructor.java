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

	private Constructor(final ClassWriter cw) {
		this.cw = cw;
		mv = this.cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
	}
	
	static Constructor constructor(ClassWriter cw) {
		return new Constructor(cw);
	}

	@Override
	public Func init() {
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		return this;
	}
	
	@Override
	public Func declareVar(String var) {
		initVarAsgn(var);
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
	public Func invokeFunc(String name, int args, HandleType handleType) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Func initVarAsgn(String var) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Func initVarAsgnEnd() {
		// TODO Auto-generated method stub
		return this;
	}
	
	@Override
	public Func loadReturnValue() {
		return null;
	}

	@Override
	public Func declareIfCondition() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Func end() {
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		return this;
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
