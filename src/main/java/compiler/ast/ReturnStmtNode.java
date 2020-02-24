package compiler.ast;

import compiler.ast.visitor.Visitor;

public class ReturnStmtNode implements StmtNode{
	
	public ExprNode expr;
	
	public ReturnStmtNode(ExprNode expr) {
		this.expr = expr;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
