package compiler;

import java.util.HashMap;
import java.util.Map;

import compiler.ast.FuncDeclNode;
import compiler.ast.VarDeclNode;

public class SymbolTable {
	private Map<VarDeclNode,FuncDeclNode> map;
	
	public SymbolTable() {
		map = new HashMap<>();
	}
	
	public void put(VarDeclNode var, FuncDeclNode func) {
		map.put(var, func);
	}
	
	public FuncDeclNode search(VarDeclNode var) {
		return map.get(var);
	}
}
