package compiler.ast;

import java.util.Iterator;
import java.util.Vector;

public class VarDeclNodeList implements Iterable<NodeWithVarDecl>{
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

	@Override
	public Iterator<NodeWithVarDecl> iterator() {
		return new Iterator<NodeWithVarDecl>() {
			int i = 0;
			@Override
			public boolean hasNext() {
				if(i < size()) 
					return true;
				return false;
			}

			@Override
			public NodeWithVarDecl next() {
				return elementAt(i++);
			}
			
		};
	}
}
