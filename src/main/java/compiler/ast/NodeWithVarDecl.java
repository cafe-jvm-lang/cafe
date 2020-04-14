package compiler.ast;

import compiler.visitor.Visitor;

public interface NodeWithVarDecl extends Node{
	void accept(Visitor v);
}
