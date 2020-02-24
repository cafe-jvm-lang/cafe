package compiler.ast;

import compiler.ast.visitor.Visitor;

public class IntegerLiteral implements ExprNode{
	
	public int i;
	
	public IntegerLiteral(int i) {
		this.i = i;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
