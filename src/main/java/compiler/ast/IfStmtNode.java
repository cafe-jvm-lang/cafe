package compiler.ast;

import compiler.visitor.Visitor;

public class IfStmtNode implements StmtNode{
	
	public ExprNode cond;
	public VarDeclNodeList varL;
	public StmtNodeList stmtL;
	
	public IfStmtNode(ExprNode cond, StmtNodeList stmtL, VarDeclNodeList varL) {
		this.cond = cond;
		this.varL = varL;
		this.stmtL = stmtL;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
