package compiler.ast1;

import compiler.visitor.Visitor;

public class LiteralNode implements ExprNode{
	
	public Object value;
	
	
	
	public LiteralNode(Object value) {
		this.value = value;
	}



	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub
		
	}
}
