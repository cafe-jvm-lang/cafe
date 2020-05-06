package compiler.ast1;

import compiler.visitor.Visitor;

public class ParameterNode implements StmtNode{

	public IdentifierNode name;
	
	
	
	public ParameterNode(IdentifierNode name) {
		this.name = name;
	}



	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		
	}

}
