package compiler.ast;

import compiler.ast.visitor.Visitor;
import compiler.lexer.tokentypes.OpTokenType;

public class BinaryExprNode implements ExprNode{
 
	public ExprNode expr1;
	public OpTokenType op;
	public ExprNode expr2;
	
	public BinaryExprNode(ExprNode expr1,OpTokenType op,ExprNode expr2) {
		this.expr1 = expr1;
		this.op = op;
		this.expr2 = expr2;
	}
	
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
