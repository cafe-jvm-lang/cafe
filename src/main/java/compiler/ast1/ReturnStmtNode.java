package compiler.ast1;

import compiler.visitor.Visitor;

public class ReturnStmtNode implements ExprNode {

	public ExprNode expr;
	
	
	
	public ReturnStmtNode(ExprNode expr) {
		this.expr = expr;
	}



	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub

	}
}
