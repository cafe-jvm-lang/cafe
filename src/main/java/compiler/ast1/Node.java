package compiler.ast1;

import compiler.visitor.Visitor;

public interface Node {
	void accept(Visitor v);
}
