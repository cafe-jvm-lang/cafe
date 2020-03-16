package compiler.codegen;

public interface Opcodes {
	
	// Access Flags
	// https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.1-200-E.1
	// Not all flags are taken.
	int ACC_PUBLIC = 0x0001;
	int ACC_FINAL = 0x0010;
	int ACC_SUPER = 0x0020;
	int ACC_ABSTRACT = 0x0400;
}
