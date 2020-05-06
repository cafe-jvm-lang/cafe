package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class FuncDeclNode implements StmtNode{
	public IdentifierNode nm;
	public ParameterNodeList argL;
//	public StmtNodeList stmtL;
//	public VarDeclNodeList varL;

	public Block block;
	
	public FuncDeclNode(IdentifierNode name,ParameterNodeList argsList,Block block) {
		nm = name;
		argL = argsList;
//		stmtL = stmtList;
//		varL = varList;
		
		this.block = block;
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
