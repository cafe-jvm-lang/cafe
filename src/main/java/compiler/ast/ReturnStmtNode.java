package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class ReturnStmtNode implements StmtNode{
	
	public ExprNode expr;
	
	public ReturnStmtNode(ExprNode expr) {
		this.expr = expr;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public <R> R accept(GenericVisitor v) {
		// TODO Auto-generated method stub
		return v.visit(this);
	}
}
