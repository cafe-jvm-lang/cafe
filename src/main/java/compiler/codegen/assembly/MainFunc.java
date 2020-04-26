package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

public class MainFunc implements Func {

	private final ClassWriter cw;
	private final MethodVisitor mv;
	private final String clazz;

	private static int P_S = ACC_PRIVATE | ACC_STATIC;

	private int arr = 1;
	
	protected MainFunc(final ClassWriter cw, final String className) {
		this.cw = cw;
		clazz = className;
		mv = this.cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
	}

	@Override
	public Func visitCode() {
		mv.visitCode();
		return this;
	}

	@Override
	public Func visitVarAsgn(String var, Object e) {
		ExprFunc expr = (ExprFunc) e;
		cw.visitField(P_S, var, "Ljava/lang/Object;", null, null).visitEnd();
		createGetterSetter(var);
		String packageCLazz = clazz.split("/")[0]+"."+clazz.split("/")[1];
		visitFuncInvk(expr.name, expr.exprFuncDescriptor, ExprFunc.makeHandle("MethodInvocation", "[Ljava/lang/Object;"), packageCLazz);
//		mv.visitVarInsn(ASTORE, arr++);
//		mv.visitVarInsn(ALOAD, arr-1);
		
		visitFuncInvk(var, "(Ljava/lang/Object;)V", ExprFunc.makeHandle("MethodInvocation", "[Ljava/lang/Object;"),packageCLazz);
		return this;
	}

	@Override
	public Func visitFuncInvk(String name, String descriptor, Handle handle, Object... args) {
		if (args != null) {
			mv.visitInvokeDynamicInsn(name, descriptor, handle, args);
		} else {
			mv.visitInvokeDynamicInsn(name, descriptor, handle);
		}
		return this;
	}

	@Override
	public Func visitEnd() {
		mv.visitInsn(RETURN);
		mv.visitMaxs(0,0);
		mv.visitEnd();
		return this;
	}

	@Override
	public ExprFunc visitExpr(String funcName) {
		return new ExprFunc(cw, funcName);
	}

	private void createGetterSetter(String name) {
		MethodVisitor field = cw.visitMethod(P_S, name, "()Ljava/lang/Object;", null, null);
		field.visitCode();
		field.visitFieldInsn(GETSTATIC, clazz, name, "Ljava/lang/Object;");
		field.visitInsn(ARETURN);
		field.visitMaxs(0, 0);
		field.visitEnd();

		field = cw.visitMethod(P_S, name, "(Ljava/lang/Object;)V", null, null);
		field.visitCode();
		field.visitVarInsn(ALOAD, 0);
		field.visitFieldInsn(PUTSTATIC, clazz, name, "Ljava/lang/Object;");
		field.visitInsn(RETURN);
		field.visitMaxs(0, 0);
		field.visitEnd();
	}
}
