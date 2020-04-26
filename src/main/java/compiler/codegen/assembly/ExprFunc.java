package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

public class ExprFunc extends SimpleFunc{
	
	private final MethodVisitor mv;
	private Stack stack;
	final String operatorDescriptor = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
	String exprFuncDescriptor = "()Ljava/lang/Object;";
	String name;
	
	static final Handle OPERATOR_HANDLE = makeHandle(
		      "OperatorInvocation", "I");
	
	public class Stack{
		int top=-1;
		List<Object> stack;
		
		public Stack() {
			stack = new ArrayList<>();
		}
		
		public void push(Object o) {
			stack.add(o);
			top++;
			System.out.println(stack);
		}
		
		public Object pop() {
			if(top>-1)
				return stack.remove(top--);
			return null;
		}
		
		
	}
	
	ExprFunc(ClassWriter cw, String name) {
		super(cw, name, "()Ljava/lang/Object;",ACC_PRIVATE | ACC_STATIC );
		this.name = name;
		this.mv = super.mv;
		stack = new Stack();
		mv.visitCode();
	}
	
	public void genExprFunc() {
		Object ob = stack.pop();
		String opFuncName;
		int i=-1,load=0;
		while(ob!= null ) {
			List<Object> tmp1 = new ArrayList<Object>();
			while(ob.toString().charAt(0)!= '#') {
				tmp1.add(ob);
				load++;
				ob = stack.pop();
			}
			
			for(int itr= tmp1.size()-1; itr>= 0 ;itr--) {
				loadObject(tmp1.get(itr));
			}
//			if(ob.toString().charAt(0) != '#') {
//				loadObject(ob);
//				load++;
//			}
//			else {
				int j = i;
				while(load < 2 && j >-1) {
					mv.visitVarInsn(ALOAD, j--);
					load++;
				}
				opFuncName = ob.toString().split("#")[1];
				visitFuncInvk(opFuncName, operatorDescriptor);
				mv.visitVarInsn(ASTORE, ++i);
				load = 0;
			//}
			ob = stack.pop();
		}
		mv.visitVarInsn(ALOAD, i);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0,0);
		mv.visitEnd();
		
	}
	
	private void visitFuncInvk(String name,String descriptor) {
		mv.visitInvokeDynamicInsn(name, descriptor, OPERATOR_HANDLE,(Integer) 2);
	}
	
	public Stack getExprStack() {
		return stack;
	}
	
	private ExprFunc loadObject(Object value) {
		if(value instanceof Number) {
			BytecodeUtils.loadToOperandStack(mv, value);
		}
		else {
			// For variable identifiers ...
		}
		return this;
	}
	
	static Handle makeHandle(String className, String description) {
	    return new Handle(H_INVOKESTATIC,
	      "compiler/codegen/runtime/" + className,
	      "bootstrap",
	      "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;"
	      + description + ")Ljava/lang/invoke/CallSite;", false);
	  }
}
