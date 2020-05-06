package compiler.ast;

import java.util.Iterator;
import java.util.Vector;

public class ParameterNodeList implements Iterable<ParameterNode>{
	private Vector<ParameterNode> list;

	public ParameterNodeList() {
		list = new Vector<>();
	}

	public void addElement(ParameterNode n) {
		list.addElement(n);
	}

	public ParameterNode elementAt(int i) {
		return list.elementAt(i);
	}

	public void addAll(ParameterNodeList varL) {
		for(int i=0;i < varL.size(); i++) {
			list.add(varL.elementAt(i));
		}
	}
	
	public int size() {
		return list.size();
	}

	@Override
	public Iterator<ParameterNode> iterator() {
		return new Iterator<ParameterNode>() {
			int i = 0;
			@Override
			public boolean hasNext() {
				if(i < size()) 
					return true;
				return false;
			}

			@Override
			public ParameterNode next() {
				return elementAt(i++);
			}
			
		};
	}
}
