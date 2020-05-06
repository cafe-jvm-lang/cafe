package compiler.ast1;

import compiler.visitor.Visitor;

public class VarDeclNode implements StmtNode {

	public IdentifierNode name;

	public VarDeclNode(IdentifierNode name) {
		this.name = name;
	}

	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub

	}
}
