package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Func {
	
	final ClassWriter cw;
	MethodVisitor mv;
	final Bootstrapper bootstapper;
	boolean isMain;
	String className;
	final Handle bootstrapHandle; 
	Func(ClassWriter cw, boolean isMain,String className) {
		this.cw = cw;
		this.isMain = isMain;
		bootstapper = new Bootstrapper(cw);
		bootstapper.bootstrap();
		this.className = className;
		bootstrapHandle = new Handle(Opcodes.H_INVOKESTATIC, className, "bootstrapDynamic", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false);
		if(isMain)
			mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
	}
	
	private String createDescriptor(int argsLen,boolean hasReturnVal) {
		String desc = "(";
		for(int i=0;i<argsLen;i++) {
			desc += "Ljava/lang/Object;";
		}
		desc+=")";
		if(hasReturnVal)
			desc+="Ljava/lang/Object;";
		else
			desc+="V";
		return desc;
	}
	
	void createFunc(String nm,int argsLen,boolean hasReturnVal) {
		if(!isMain) {
			String desc = createDescriptor(argsLen, hasReturnVal);
			mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, nm, desc, null, null);
		}
	}
	
	void invokeDynamic(String nm,int argsLen,boolean hasReturnVal) {
		String desc = createDescriptor(argsLen, hasReturnVal);
		mv.visitInvokeDynamicInsn(nm, desc, bootstrapHandle, new Object() {});
	}
}
