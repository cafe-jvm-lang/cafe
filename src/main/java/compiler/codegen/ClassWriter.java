package compiler.codegen;

public class ClassWriter {
	private ByteVector vector;
	
	private ConstantPoolTable constantPoolTable;
	private int version;
	private String className;
	
	public ClassWriter() {
		constantPoolTable = new ConstantPoolTable(this);
	}
	
	public void visit(int version,
			String className) {
		this.version = version;
		this.className = className;
	}
	
	public byte[] toByteArray() {
		// Min size of bytearray
		int size= 24;
		
		size += constantPoolTable.getConstantPoolLength();
		ByteVector result = new ByteVector(size);
		result.putInt(0xCAFEBABE);
		
		return result.data;
	}
}
