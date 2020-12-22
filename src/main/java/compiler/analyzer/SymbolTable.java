package compiler.analyzer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

final class Symbol{
	String name;

	public Symbol(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return name.equals(symbol.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}

public class SymbolTable {
	public final SymbolTable parent;
	
	private final Set<Symbol> symbols;

	private boolean canDeclare = true;
	
	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
		symbols = new HashSet<>();
	}

	public SymbolTable notDeclarable(){
		canDeclare = false;
		return this;
	}

	public boolean insert(Symbol n) {
		if(!canDeclare){
			if(parent.isPresent(n.name)){
				return false;
			}
		}
		return symbols.add(n);
	}
	
	public boolean isPresent(String n) {
		SymbolTable table = this;
		Symbol s = new Symbol(n);
		while(table != null) {
			if(table.symbols.contains(s))
				return true;
			table = table.parent;
		}
		return false;
	}
}
