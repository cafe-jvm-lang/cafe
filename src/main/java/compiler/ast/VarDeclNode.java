package compiler.ast;

import compiler.ast.visitor.Visitor;

public class VarDeclNode implements Node,NodeWithVarDecl{
	public IdentifierNode nm;
	
	public VarDeclNode(IdentifierNode nm) {
		this.nm = nm;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);	
	}
}
