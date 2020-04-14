package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class IfElseStmtNode implements StmtNode {

	public ExprNode ifCond;
	public StmtNodeList ifStmtL;
	public VarDeclNodeList ifVarL;
	public IfStmtNode ifNode;
	
	public StmtNodeList elseStmtL;
	public VarDeclNodeList elseVarL;
	public ElseStmtNode elseNode;

	public IfElseStmtNode(ExprNode ifCond, StmtNodeList ifStmtL, VarDeclNodeList ifVarL, StmtNodeList elseStmtL, VarDeclNodeList elseVarL) {
		this.ifCond = ifCond;
		this.ifStmtL = ifStmtL;
		this.ifVarL = ifVarL;
		
		ifNode = new IfStmtNode(ifCond, ifStmtL, ifVarL);
		
		this.elseStmtL = elseStmtL;
		this.elseVarL = elseVarL;
		
		elseNode = new ElseStmtNode(elseStmtL, elseVarL);
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
