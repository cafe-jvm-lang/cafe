package compiler.ast;

import compiler.ast.visitor.Visitor;

public class VarDeclNode implements Node{
	String nm;
	
	public VarDeclNode(String nm) {
		this.nm = nm;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);	
	}
}
