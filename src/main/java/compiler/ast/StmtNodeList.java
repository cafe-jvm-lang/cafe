package compiler.ast;

import java.util.Vector;

public class StmtNodeList {
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
}
