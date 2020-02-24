package compiler.ast;

import compiler.ast.visitor.Visitor;

public class MethodCall implements ExprNode{
	
	public String iden;
	public ArgsNodeList argsL;
	
	public MethodCall(String iden, ArgsNodeList argsL) {
		this.iden = iden;
		this.argsL = argsL;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
