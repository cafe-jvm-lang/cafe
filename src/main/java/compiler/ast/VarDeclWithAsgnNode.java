package compiler.ast;

import compiler.ast.visitor.Visitor;

public class VarDeclWithAsgnNode implements Node,NodeWithVarDecl{
	
	public VarDeclNode nm;
	public ExprNode val;
	
	public VarDeclWithAsgnNode(VarDeclNode nm,ExprNode val) {
		this.nm = nm;
		this.val = val;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
