package compiler.ast;

import compiler.visitor.Visitor;

public class ArgsNode implements Node {
	public IdentifierNode arg;
	
	public ArgsNode(IdentifierNode arg) {
		this.arg = arg;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
