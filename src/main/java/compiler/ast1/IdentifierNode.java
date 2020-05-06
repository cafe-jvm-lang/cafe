package compiler.ast1;

import compiler.ast1.ExprNode;
import compiler.visitor.Visitor;

public class IdentifierNode implements ExprNode{

	public String name;
	
	
	
	public IdentifierNode(String name) {
		this.name = name;
	}



	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		
	}

}
