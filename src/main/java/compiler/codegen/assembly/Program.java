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

import compiler.SymbolTable;
import compiler.SymbolTableMapper;

public class Program{
	private final ClassWriter cw;
	public final String clazz;

	private final Func constructor;
	private final Func mainFunc;
	private int block;
	
	private final Map<String, FieldVisitor> fields;
	private final Map<String, Func> functions;

	final static SymbolTable globalSymbolTable = SymbolTableMapper.globalSymbolTable();
	
	static String packageClazz;
	
	private Program(final String fileName) {
		clazz = fileName.split("\\.")[0];
		
		packageClazz = clazz.split("/")[0]+"."+clazz.split("/")[1];

		cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
		cw.visit(V10, ACC_PUBLIC | ACC_SUPER, clazz, null, "java/lang/Object", null);
		cw.visitSource(clazz, null);

		constructor = Constructor.constructor(cw).init();
		mainFunc = MainFunc.mainFunc(cw,clazz).init();
		
		fields = new HashMap<>();
		functions = new HashMap<>();
	}
	
	public void makeDefaultConstructor() {
		constructor.end();
	}
	
	public Func initConstructor() {
		return constructor;
	}
	
	public Func initMain() {
		return mainFunc;
	}
	
	public Func initSimpleFunc(int block,String name, int args) {
		Func func = SimpleFunc.simpleFunc(cw,Func.FLAG_PUBLIC_STATIC, name, args,block).init();
		functions.put(name, func);
		return func.init();
	}
	
	public void end() {
		cw.visitEnd();
		try {
			FileOutputStream fileOpStream = new FileOutputStream("./"+clazz+".class");
			fileOpStream.write(cw.toByteArray());
			fileOpStream.close();
			System.out.println("Compiled Successfully");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public static Program newProgram(String programName) {
		return new Program(programName);
	}

}
