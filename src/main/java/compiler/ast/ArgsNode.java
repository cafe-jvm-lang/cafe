package compiler.ast;

import compiler.visitor.Visitor;

public class ArgsNode implements Node {
	public String arg;
	
	public ArgsNode(String arg) {
		this.arg = arg;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
