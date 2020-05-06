package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class Block implements Node {
	public VarDeclNodeList varL;
	public StmtNodeList stmtL;
	
	public Block(VarDeclNodeList varL,StmtNodeList stmtL) {
		this.varL = varL;
		this.stmtL = stmtL;
	}
	
	@Override
	public <R> R accept(GenericVisitor v) {
		return v.visit(this);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
