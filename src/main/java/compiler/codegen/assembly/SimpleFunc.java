package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

import compiler.utils.HandleType;

public class SimpleFunc implements Func {

	private final ClassWriter cw;
	protected final MethodVisitor mv;

	protected SimpleFunc(final ClassWriter cw, final String name, final String descriptor) {
		this(cw, name, descriptor, ACC_PUBLIC);
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
	public Func visitEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExprFunc visitExpr(String funcName) {
		return new ExprFunc(cw, funcName);
	}

	@Override
	public Func visitVarAsgn(String var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Func visitVarAsgnEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Func visitFuncInvk(String name, int args, HandleType handleType, Object... extraArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Func loadLiteral(Object num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Func loadIdentifier(String idName) {
		// TODO Auto-generated method stub
		return null;
	}
}
