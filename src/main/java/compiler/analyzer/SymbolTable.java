package compiler.analyzer;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public final SymbolTable parent;

    private final Map<String,Symbol> symbols;

    private boolean canDeclare = true;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        symbols = new HashMap<>();
    }

    public SymbolTable notDeclarable() {
        canDeclare = false;
        return this;
    }

    public boolean insert(Symbol n) {
        if (!canDeclare) {
            if (parent.isSymbolPresent(n.name)) {
                return false;
            }
        }
        if(symbols.containsKey(n.name))
            return false;
        else {
            symbols.put(n.name, n);
            return true;
        }
    }

    public boolean isSymbolPresent(String n) {
        SymbolTable table = this;
        while (table != null) {
            if (table.symbols.containsKey(n))
                return true;
            table = table.parent;
        }
        return false;
    }

    public boolean isSymbolConstant(String n) {
        SymbolTable table = this;
        while (table != null) {
            if (table.symbols.containsKey(n)){
                if(table.symbols.get(n).isConst)
                    return true;
            }
            table = table.parent;
        }
        return false;
    }

//    public Symbol get(String n){
//        SymbolTable table = this;
//        Symbol s = new Symbol(n);
//        while (table != null) {
//            if (table.symbols.contains(s))
//                ;
//            table = table.parent;
//        }
//        return false;
//    }
}
