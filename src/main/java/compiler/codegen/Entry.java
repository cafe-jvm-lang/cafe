package compiler.codegen;

class Entry extends Constant{
	Entry next;
	int hashCode;
	public Entry(int index, int tag, String value, int hashCode) {
		super(index, tag, value);
		this.hashCode = hashCode;
	}
}
