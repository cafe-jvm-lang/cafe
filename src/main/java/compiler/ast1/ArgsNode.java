package compiler.ast1;

import compiler.visitor.Visitor;

public class ArgsNode<T> implements ExprNode{

	public T name;
	
	public ArgsNode(T name) {
		this.name = name;
	}
	
	@Override
	public void accept(Visitor v) {
		
	}

}
