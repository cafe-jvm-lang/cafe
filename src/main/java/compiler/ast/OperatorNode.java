package compiler.ast;

import compiler.ast.visitor.Visitor;
import compiler.lexer.tokentypes.TokenType.OpTokenType;

public class OperatorNode implements Node {
	public OpTokenType type;

	public OperatorNode(OpTokenType type) {
		this.type = type;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);

	}
}
