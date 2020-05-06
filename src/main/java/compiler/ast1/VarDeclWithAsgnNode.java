package compiler.ast1;

import compiler.visitor.Visitor;

public class VarDeclWithAsgnNode implements StmtNode {

	public IdentifierNode name;
	public ExprNode value;

	public VarDeclWithAsgnNode(IdentifierNode name, ExprNode value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub

	}
}
