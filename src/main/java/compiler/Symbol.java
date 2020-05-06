package compiler;

import compiler.ast.Node;
import compiler.utils.Scope;
import compiler.utils.SymbolType;

/**
 * @author Dhyey
 *
 */
public class Symbol {
	public String name;
	public SymbolType symType;
	//public Scope scope;
	public Node ogNode;
	
	
	/**
	 * For SymbolType - Func
	 * 		stores number of args of that function
	 * For SymbolType - var
	 * 		stores position of variable in local variable array
	 */
	public Integer args;
	

	public Symbol(String name, SymbolType symType, Node ogNode,Integer args) {
		this.name = name;
		this.symType = symType;
		//this.scope = scope;
		this.ogNode = ogNode;
		this.args = args;
	}
	
	public void print() {
		System.out.println("Identifier Node:"+name);
		System.out.println("Symbol Type:"+symType);
		//System.out.println("Scope:"+scope);
		System.out.println("Og Node:"+ogNode);
		System.out.println("Args:"+args);
	}
}
