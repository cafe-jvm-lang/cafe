package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class FuncDeclNode implements StmtNode{
	public IdentifierNode nm;
	public ArgsNodeList argL;
	public StmtNodeList stmtL;
	public VarDeclNodeList varL;
	
	public FuncDeclNode(IdentifierNode name,ArgsNodeList argsList,StmtNodeList stmtList,VarDeclNodeList varList) {
		nm = name;
		argL = argsList;
		stmtL = stmtList;
		varL = varList;
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
