package compiler;

import compiler.ast.IdentifierNode;
import compiler.ast.Node;
import compiler.utils.Scope;
import compiler.utils.SymbolType;
import compiler.utils.Type;

public class Symbol {
	IdentifierNode node;
	SymbolType symType;
	Scope scope;
	Type type;
	public Node ogNode;
	public Integer args; 

	public Symbol(IdentifierNode node, SymbolType symType, Scope scope, Type type, Node ogNode,Integer args) {
		this.node = node;
		this.symType = symType;
		this.scope = scope;
		this.type = type;
		this.ogNode = ogNode;
		this.args = args;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public void print() {
		System.out.println("Identifier Node:"+node.id);
		System.out.println("Symbol Type:"+symType);
		System.out.println("Scope:"+scope);
		System.out.println("Type:"+type);
		System.out.println("Og Node:"+ogNode);
		System.out.println("Args:"+args);
	}
}
