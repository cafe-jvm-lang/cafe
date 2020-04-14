package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class VarDeclWithAsgnNode implements StmtNode,NodeWithVarDecl{
	
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

	@Override
	public <R> R accept(GenericVisitor v) {
		// TODO Auto-generated method stub
		return v.visit(this);
	}
}
