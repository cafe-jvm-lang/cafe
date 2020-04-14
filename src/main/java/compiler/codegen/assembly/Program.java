package compiler.codegen.assembly;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.V10;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

public class Program{
	private final ClassWriter cw;
	private final String className;

	private final Map<String, FieldVisitor> fields;
	private final Map<String, SimpleFunc> functions;
	private final Constructor constructor;
	private final MainFunc mainFunc;

	public Program(final String fileName) {
		className = fileName.split("\\.")[0];

		cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
		cw.visit(V10, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(className, null);
		
		fields = new HashMap<>();
		functions = new HashMap<>();
		constructor = new Constructor(cw);
		mainFunc = new MainFunc(cw);
	}
	
	public Func visitConstructor() {
		return constructor.visitCode();
	}
	
	public Func visitMain() {
		return mainFunc.visitCode();
	}
	
	public Func visitFunc(String name, String descriptor) {
		SimpleFunc func = new SimpleFunc(cw, name, descriptor);
		functions.put(name, func);
		return func.visitCode();
	}
	
	public Func visitField(String name, Object value) {
		if(value instanceof Integer) {
			
		}
		return constructor.visitVarAsgn(name,value);
	}
	
	public void visitEnd() {
		cw.visitEnd();
		try {
			FileOutputStream fileOpStream = new FileOutputStream("./"+className+".class");
			fileOpStream.write(cw.toByteArray());
			fileOpStream.close();
			System.out.println("Compiled Successfully");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}

}
