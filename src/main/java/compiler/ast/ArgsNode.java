package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class ArgsNode<T> implements Node {
	//public IdentifierNode arg;
	public T arg;
	public boolean isDecl;
	
	public ArgsNode(T arg, boolean isDecl) {
		this.arg = arg;
		this.isDecl = isDecl;
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
