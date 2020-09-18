package compiler.analyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import compiler.ast.Node.IdenNode;

public class SymbolTable {
	public final SymbolTable parent;
	
	private final Set<IdenNode> symbols;
	
	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
		symbols = new HashSet<>();
	}
	
	public void insert(IdenNode n) {
		symbols.add(n);
	}
	
	public void insertAll(Collection<? extends IdenNode> n) {
		symbols.addAll(n);
	}
	
	public boolean isPresent(IdenNode n) {
		SymbolTable table = this;
		while(table != null) {
			if(table.symbols.contains(n))
				return true;
			table = table.parent;
		}
		return false;
	}
}
