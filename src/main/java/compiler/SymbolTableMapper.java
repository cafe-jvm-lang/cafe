package compiler;

import java.util.HashMap;
import java.util.Map;

public final class SymbolTableMapper {
	
	public final static SymbolTable globalSymbolTable = new SymbolTable();
	public final static Map<String, SymbolTable> mapper = new HashMap<>();
	
	{
		globalSymbolTable.setParent(null);
	}
	
	
	public static void addSymbolTable(String name,SymbolTable table) {
		mapper.put(name,table);
	}
	
	public  static SymbolTable getSymbolTable(String name) {
		return mapper.get(name);
	}
}
