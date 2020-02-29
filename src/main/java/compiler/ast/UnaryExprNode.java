package compiler.ast;

import compiler.ast.visitor.Visitor;
import compiler.lexer.tokentypes.TokenType;
import compiler.lexer.tokentypes.TokenType.OpTokenType;

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
