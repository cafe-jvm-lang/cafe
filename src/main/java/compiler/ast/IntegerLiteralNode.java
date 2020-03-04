package compiler.ast;

import compiler.visitor.Visitor;

public class IntegerLiteralNode implements ExprNode{
	
	public int i;
	
	public IntegerLiteralNode(int i) {
		this.i = i;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
