package compiler.ast;

import compiler.ast.visitor.Visitor;

public interface Node {
	public void accept(Visitor v);
}
