package compiler.codegen.assembly;

import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;

public class Program {
	ClassWriter cw;
	
	public final String className;
	
	public Program(final String fileName) {
		cw = new ClassWriter(0);
		className = fileName;
		
		cw.visit(V10, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(className, null);
	}
	
	public Func genMainFunc() {
		return new Func(cw,true,className);
	}
	
	
}
