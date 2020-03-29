package compiler.codegen;

public class MethodWriter {
	int access_flags;
	String methodName;
	int name_index;
	int descriptor_index;
	private SymbolTable constantPoolTable;
	private ByteVector methodVector;
	
	public MethodWriter() {
		methodVector = new ByteVector();
	}
	
	public void visit(int access,String name, SymbolTable constantPoolTable) {
		this.access_flags = access;
		this.methodName = name;
		this.constantPoolTable = constantPoolTable;
	}
}
