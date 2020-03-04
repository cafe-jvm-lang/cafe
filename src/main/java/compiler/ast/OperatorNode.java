package compiler.ast;

import compiler.lexer.tokentypes.TokenType.OpTokenType;
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
}
