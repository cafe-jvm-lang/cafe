package compiler.ast1;

import java.util.List;

import compiler.visitor.Visitor;

public class Block implements Node{

	public List<StmtNode> stmtNodeList;
	
	public Block(List<StmtNode> stmtNodeList) {
		this.stmtNodeList = stmtNodeList;
	}



	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		
	}

}
