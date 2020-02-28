package compiler.ast;

import compiler.ast.visitor.Visitor;

public class VarDeclWithAsgnNode implements Node{
	
	public IdentifierNode nm;
	public ExprNode val;
	
	public VarDeclWithAsgnNode(IdentifierNode nm) {
		this(nm,null);
	}
	
	public VarDeclWithAsgnNode(IdentifierNode nm,ExprNode val) {
		this.nm = nm;
		this.val = val;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
