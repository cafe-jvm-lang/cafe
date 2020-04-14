package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public interface Node {
	void accept(Visitor v);
	<R> R accept(GenericVisitor v);
}
