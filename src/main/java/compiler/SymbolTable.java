package compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import compiler.utils.SymbolType;

public class SymbolTable {
	private List<Symbol> symL;
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
				.anyMatch(e -> e.name.equals(sym.name) 
				          && e.symType == sym.symType
				          )) {
			symL.add(sym);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasSymbol(final String n, final SymbolType type) {
 		return symL.stream()
				   .anyMatch(e -> e.name.equals(n) 
						   && e.symType == type);
	}
	
	public boolean setSymbol(Symbol symbol) {
		int index = symL.indexOf(getSymbol(symbol.name, symbol.symType));
		if(index != -1) {
			symL.set(index, symbol);
			return true;
		}
		return false;
	}
	
	public Symbol getSymbol(final String n, final SymbolType type) {
 		return symL.stream()
				   .filter(e -> e.name.equals(n) && e.symType == type)
				   .findFirst()
				   .orElse(null);
	}
	
	public Symbol getSymbol(Predicate<Symbol> c) {
		return symL.stream()
				   .filter(c)
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
