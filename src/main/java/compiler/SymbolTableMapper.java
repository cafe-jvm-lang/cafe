package compiler;

import java.util.HashMap;
import java.util.Map;

public final class SymbolTableMapper {
	
	private final static SymbolTable globalSymbolTable = new SymbolTable();
	private final static Map<Integer, SymbolTable> mapper = new HashMap<>();	
	
	static
	{
		globalSymbolTable.setParent(null);
		mapper.put(0, globalSymbolTable);
	}
	
	/**
	 * Private constructor to avoid initialization
	 */
	private SymbolTableMapper(){}
	
	public static boolean addSymbolTable(int id,SymbolTable table) {
		if(mapper.containsKey(id))
			return false;
		mapper.put(id,table);
		return true;
	}
	
	public static SymbolTable globalSymbolTable() {
		return globalSymbolTable;
	}
	
	public  static SymbolTable getSymbolTable(int id) {
		return mapper.get(id);
	}
	
	public static void print() {
		mapper.entrySet().forEach(entry->{
		    System.out.println(entry.getKey() + " " + entry.getValue());  
		 });
	}
}
