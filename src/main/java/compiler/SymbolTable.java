package compiler;

import java.util.ArrayList;
import java.util.List;

import compiler.ast.IdentifierNode;
import compiler.utils.SymbolType;

public class SymbolTable {
	private static List<Symbol> symL;
	private SymbolTable parent;
	
	public SymbolTable() {
		symL = new ArrayList<>();
	}
	
	
//	public List<Symbol> getSymbolList(){
//		return symL;
//	}
//	
	public boolean addSymbol(Symbol sym) {
		if(!symL.stream()
		   .anyMatch(e -> e.node.id.equals(sym.node.id) 
				          && e.symType == sym.symType
				          && e.scope == sym.scope)) {
			
			symL.add(sym);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasSymbol(final IdentifierNode n, final SymbolType type) {
 		return symL.stream()
				   .anyMatch(e -> e.node.id.equals(n.id) 
						   && e.symType == type);
	}
	
	public Symbol getSymbol(final IdentifierNode n, final SymbolType type) {
 		return symL.stream()
				   .filter(e -> e.node.id.equals(n.id) && e.symType == type)
				   .findFirst()
				   .orElse(null);
	}
	
	public void setParent(SymbolTable parent) {
		this.parent = parent;
	}
	
	public SymbolTable getParent() {
		return parent;
	}
}
