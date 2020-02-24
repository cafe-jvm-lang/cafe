package compiler.ast;

import java.util.Vector;

public class ArgsNodeList {
	private Vector<ArgsNode> list;
	
	public ArgsNodeList() {
		list = new Vector<>();
	}
	
	public void addElement(ArgsNode n) {
		list.addElement(n);
	}
	
	public ArgsNode elementAt(int i) {
		return list.elementAt(i);
	}
	
	public int size() {
		return list.size();
	}
}
