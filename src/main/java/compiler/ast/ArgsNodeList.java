package compiler.ast;

import java.util.Iterator;
import java.util.Vector;

public class ArgsNodeList implements Iterable<ArgsNode<?>>{
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
	
	@Override
	public Iterator<ArgsNode<?>> iterator() {
		return new Iterator<ArgsNode<?>>() {
			int i = 0;
			@Override
			public boolean hasNext() {
				if(i < size()) 
					return true;
				return false;
			}

			@Override
			public ArgsNode<?> next() {
				return elementAt(i++);
			}
			
		};
	}
}
