package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class ParameterNode implements Node{

	public IdentifierNode n;
	
	public ParameterNode(IdentifierNode n) {
		this.n = n;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public <R> R accept(GenericVisitor v) {
		return v.visit(this);
	}

}
