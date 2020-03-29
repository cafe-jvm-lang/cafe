package compiler.codegen.assembly;

import org.objectweb.asm.ClassWriter;

public class Program {
	ClassWriter cw;
	
	private final String className;
	
	public Program(final String fileName) {
		cw = new ClassWriter(0);
		className = fileName;
	}
}
