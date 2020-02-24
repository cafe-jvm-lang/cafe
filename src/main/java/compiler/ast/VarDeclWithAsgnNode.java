package compiler.ast;

import compiler.ast.visitor.Visitor;

public class VarDeclWithAsgnNode implements Node{
	
	public String nm;
	public ExprNode val;
	
	public VarDeclWithAsgnNode(String nm) {
		this(nm,null);
	}
	
	public VarDeclWithAsgnNode(String nm,ExprNode val) {
		this.nm = nm;
		this.val = val;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
