package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class IfElseStmtNode implements StmtNode {

	public ExprNode ifCond;
//	public StmtNodeList ifStmtL;
//	public VarDeclNodeList ifVarL;
	public IfStmtNode ifNode;
	public Block ifBlock;
	
//	public StmtNodeList elseStmtL;
//	public VarDeclNodeList elseVarL;
	public ElseStmtNode elseNode;
	public Block elseBlock;

	public IfElseStmtNode(ExprNode ifCond, Block ifBlock, Block elseBlock) {
		this.ifCond = ifCond;
//		this.ifStmtL = ifStmtL;
//		this.ifVarL = ifVarL;
		
		this.ifBlock = ifBlock;
		
		ifNode = new IfStmtNode(ifCond, ifBlock);
		
//		this.elseStmtL = elseStmtL;
//		this.elseVarL = elseVarL;

		this.elseBlock = elseBlock;
		elseNode = new ElseStmtNode(elseBlock);
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
