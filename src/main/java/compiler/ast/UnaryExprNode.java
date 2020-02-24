package compiler.ast;

import compiler.ast.visitor.Visitor;
import compiler.lexer.tokentypes.OpTokenType;

public class UnaryExprNode implements ExprNode{
	public OpTokenType op;
	public ExprNode expr;

	public UnaryExprNode(OpTokenType op, ExprNode expr) {
		this.op = op;
		this.expr = expr;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
