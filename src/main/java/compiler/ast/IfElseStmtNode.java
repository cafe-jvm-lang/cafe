package compiler.ast;

import compiler.visitor.Visitor;

public class IfElseStmtNode implements StmtNode {

	public ExprNode ifCond;
	public StmtNodeList ifStmtL;
	public VarDeclNodeList ifVarL;

	public StmtNodeList elseStmtL;
	public VarDeclNodeList elseVarL;

	public IfElseStmtNode(ExprNode ifCond, StmtNodeList ifStmtL, VarDeclNodeList ifVarL, StmtNodeList elseStmtL, VarDeclNodeList elseVarL) {
		this.ifCond = ifCond;
		this.ifStmtL = ifStmtL;
		this.ifVarL = ifVarL;

		this.elseStmtL = elseStmtL;
		this.elseVarL = elseVarL;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
