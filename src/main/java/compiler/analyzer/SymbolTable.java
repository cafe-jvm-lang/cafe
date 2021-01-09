/*
 * Copyright (c) 2021. Dhyey Shah <dhyeyshah4@gmail.com> 
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

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
