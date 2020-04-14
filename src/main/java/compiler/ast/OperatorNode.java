package compiler.ast;

import compiler.lexer.tokentypes.TokenType.OpTokenType;
import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class OperatorNode implements ExprNode {
	public OpTokenType type;

	public OperatorNode(OpTokenType type) {
		this.type = type;
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
