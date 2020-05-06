package compiler.ast1;

import java.util.List;

import compiler.visitor.Visitor;

public class FuncCallNode implements ExprNode {

	public IdentifierNode name;
	public List<ArgsNode> argsNodeList;
	
	public FuncCallNode(IdentifierNode name, List<ArgsNode> argsNodeList) {
		this.name = name;
		this.argsNodeList = argsNodeList;
	}

	@Override
	public void accept(Visitor v) {
		
	}

}
