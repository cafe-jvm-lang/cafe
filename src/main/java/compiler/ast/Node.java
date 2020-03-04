package compiler.ast;

import compiler.visitor.Visitor;

public interface Node {
	public void accept(Visitor v);
}
