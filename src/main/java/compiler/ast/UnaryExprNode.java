package compiler.ast;

import compiler.lexer.tokentypes.TokenType;
import compiler.lexer.tokentypes.TokenType.OpTokenType;
import compiler.visitor.Visitor;

public class UnaryExprNode implements ExprNode{
	public OperatorNode op;
	public ExprNode expr;

	
	public UnaryExprNode(OperatorNode op, ExprNode expr) {
		this.op = op;
		this.expr = expr;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
	
	
}
