package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class SimpleFunc implements Func{

	private final ClassWriter cw;
	protected final MethodVisitor mv;
	
	protected SimpleFunc(final ClassWriter cw, final String name, final String descriptor) {
		this(cw,name, descriptor,ACC_PUBLIC);
	}
	
	protected SimpleFunc(final ClassWriter cw, final String name, final String descriptor, int modifiers) {
		this.cw = cw;
		mv = cw.visitMethod(modifiers, name, descriptor, null, null);
	}
	
	@Override
	public Func visitCode() {
		mv.visitCode();
		return this;
	}

	@Override
	public Func visitVarAsgn(String var, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Func visitFuncInvk() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Func visitEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprFunc visitExpr(String funcName) {
		return new ExprFunc(cw, funcName);
	}
}
