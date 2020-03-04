package compiler.ast;

import compiler.visitor.Visitor;

public class VarDeclNode implements StmtNode,NodeWithVarDecl{
	public IdentifierNode nm;
	
	public VarDeclNode(IdentifierNode nm) {
		this.nm = nm;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);	
	}
}
