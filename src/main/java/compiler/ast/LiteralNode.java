package compiler.ast;

import compiler.visitor.GenericVisitor;
import compiler.visitor.Visitor;

public class LiteralNode implements ExprNode, NodeWithArgsType, NodeWithTerminalExpr{
	
	public Object i;
	
	public LiteralNode(Object i) {
		this.i = i;
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
