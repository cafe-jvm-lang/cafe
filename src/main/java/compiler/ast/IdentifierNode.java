package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class IdentifierNode implements ExprNode, NodeWithArgsType, NodeWithTerminalExpr{

	public String id;

	public IdentifierNode(String id) {
		this.id = id;
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
