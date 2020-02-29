package compiler.ast;

import java.util.Vector;

public class VarDeclNodeList {
	private Vector<NodeWithVarDecl> list;

	public VarDeclNodeList() {
		list = new Vector<>();
	}

	public void addElement(NodeWithVarDecl n) {
		list.addElement(n);
	}

	public NodeWithVarDecl elementAt(int i) {
		return list.elementAt(i);
	}

	public void addAll(VarDeclNodeList varL) {
		for(int i=0;i < varL.size(); i++) {
			list.add(varL.elementAt(i));
		}
	}
	
	public int size() {
		return list.size();
	}
}
