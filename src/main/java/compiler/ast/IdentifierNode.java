package compiler.ast;

import compiler.ast.visitor.Visitor;

public class IdentifierNode implements ExprNode {

	public String id;

	public IdentifierNode(String id) {
		this.id = id;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);

	}
}
