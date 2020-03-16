package compiler.codegen;

public class ClassWriter {
	private ByteVector vector;
	
	private ConstantPoolTable constantPoolTable;
	private int version;
	private String className;
	private int thisClass;
	
	public ClassWriter() {
		constantPoolTable = new ConstantPoolTable(this);
	}
	
	public void visit(int version,
			String className) {
		this.version = version;
		this.className = className;
		this.thisClass = constantPoolTable.addConstantClass(className).index;
	}
	
	public byte[] toByteArray() {
		// Min size of bytearray
		int size= 24;
		
		size += constantPoolTable.getConstantPoolLength();
		ByteVector result = new ByteVector(size);
		result.putInt(0xCAFEBABE).putInt(version);
		constantPoolTable.putConstantPool(result);
		result.putShort(0x0021);
		result.putShort(thisClass);		// u2 this_class
		result.putShort(0x0000);		// u2 super_class
		result.putShort(0x0000);		// u2 interfaces_count
		result.putShort(0x0000);		// u2 fields_count
		result.putShort(0x0000);		// u2 methods_count
		result.putShort(0x0000);		// u2 attributes_count
		return result.data;
	}
}
