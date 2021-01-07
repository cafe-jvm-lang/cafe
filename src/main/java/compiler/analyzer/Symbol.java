package compiler.analyzer;

import java.util.Objects;

public final class Symbol {
    String name;
    boolean isConst;

    public Symbol(String name) {
        this(name, false);
    }

    public Symbol(String name, boolean isConst) {
        this.name = name;
        this.isConst = isConst;
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
