package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class ElseStmtNode implements StmtNode {

	public StmtNodeList elseStmtL;
	public VarDeclNodeList elseVarL;

	public ElseStmtNode(StmtNodeList elseStmtL, VarDeclNodeList elseVarL) {
		this.elseStmtL = elseStmtL;
		this.elseVarL = elseVarL;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

	@Override
	public <R> R accept(GenericVisitor v) {
		return v.visit(this);
		
	}
}
