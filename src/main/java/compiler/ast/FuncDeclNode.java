package compiler.ast;

import compiler.ast.visitor.Visitor;

public class FuncDeclNode implements Node{
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
}
