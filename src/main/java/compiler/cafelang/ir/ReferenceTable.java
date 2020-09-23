package compiler.cafelang.ir;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReferenceTable {
    private ReferenceTable parent;
    private final Map<String, SymbolReference> table = new LinkedHashMap<>();

    public ReferenceTable() {
        this(null);
    }

    public ReferenceTable(ReferenceTable parent) {
        this.parent = parent;
    }

    public ReferenceTable parent() {
        return this.parent;
    }

    public ReferenceTable add(SymbolReference reference) {
        table.put(reference.getName(), reference);
        return this;
    }

    public SymbolReference get(String name) {
        SymbolReference reference = table.get(name);
        if (reference != null)
            return reference;
        if(parent != null)
            return parent.get(name);
        return null;
    }

    public ReferenceTable fork(){
        return new ReferenceTable(this);
    }
}
