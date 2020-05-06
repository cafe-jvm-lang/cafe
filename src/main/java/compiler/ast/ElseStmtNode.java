package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class ElseStmtNode implements StmtNode {

//	public StmtNodeList elseStmtL;
//	public VarDeclNodeList elseVarL;
	
	public Block block;

	public ElseStmtNode(Block block) {
//		this.elseStmtL = elseStmtL;
//		this.elseVarL = elseVarL;
		
		this.block = block;
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
