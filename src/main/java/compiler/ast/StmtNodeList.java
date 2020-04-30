package compiler.ast;

import java.util.Iterator;
import java.util.Vector;

public class StmtNodeList implements Iterable<StmtNode>{
	private Vector<StmtNode> list;
	
	public StmtNodeList() {
		list = new Vector<>();
	}
	
	public void addElement(StmtNode n) {
		list.addElement(n);
	}
	
	public StmtNode elementAt(int i) {
		return list.elementAt(i);
	}
	
	public void addAll(StmtNodeList stmtL) {
		for(int i=0;i < stmtL.size(); i++) {
			list.add(stmtL.elementAt(i));
		}
	}
	
	public int size() {
		return list.size();
	}
	
	@Override
	public Iterator<StmtNode> iterator() {
		return new Iterator<StmtNode>() {
			int i = 0;
			@Override
			public boolean hasNext() {
				if(i < size()) 
					return true;
				return false;
			}

			@Override
			public StmtNode next() {
				return elementAt(i++);
			}
			
		};
	}
}
