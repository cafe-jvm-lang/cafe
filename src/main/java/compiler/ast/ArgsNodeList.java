package compiler.ast;

import java.util.Vector;

public class ArgsNodeList {
	private Vector<ArgsNode<? extends NodeWithArgsType>> list;
	
	public ArgsNodeList() {
		list = new Vector<>();
	}
	
	public void addElement(ArgsNode<? extends NodeWithArgsType> n) {
		list.addElement(n);
	}
	
	public ArgsNode<? extends NodeWithArgsType> elementAt(int i) {
		return list.elementAt(i);
	}
	
	public int size() {
		return list.size();
	}
}
