package compiler.ast.stmt;

import compiler.ast.expr.ExprCreator;

public class ReturnStmtNode extends StmtNode{
	private ExprCreator expr;
	
	public ReturnStmtNode(ExprCreator expr) {
		this.expr = expr;
	}
	
	public ExprCreator getExpr() {
		return expr;
	}
}
