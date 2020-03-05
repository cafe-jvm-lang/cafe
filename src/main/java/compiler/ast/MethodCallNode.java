package compiler.ast;

import compiler.visitor.Visitor;

public class MethodCallNode implements ExprNode{
	
	public IdentifierNode iden;
	public ArgsNodeList argsL;
	
	public MethodCallNode(IdentifierNode iden, ArgsNodeList argsL) {
		this.iden = iden;
		this.argsL = argsL;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
