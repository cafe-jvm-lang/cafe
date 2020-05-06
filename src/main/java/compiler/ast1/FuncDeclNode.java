package compiler.ast1;

import java.util.List;

import compiler.visitor.Visitor;

public class FuncDeclNode implements StmtNode {

	public IdentifierNode name;
	public List<ParameterNode> parameterNodeList;
	public Block functionBlock;

	public FuncDeclNode(IdentifierNode name, List<ParameterNode> parameterNodeList, Block functionBlock) {
		this.name = name;
		this.parameterNodeList = parameterNodeList;
		this.functionBlock = functionBlock;
	}

	@Override
	public void accept(Visitor v) {
		// TODO Auto-generated method stub

	}

}
