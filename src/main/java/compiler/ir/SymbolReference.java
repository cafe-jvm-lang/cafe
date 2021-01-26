/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
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

package compiler.ir;

public class SymbolReference extends CafeElement<SymbolReference> {

    @Override
    protected SymbolReference self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitSymbolReference(this);
    }

    public enum Kind {
        VAR, CONST
    }

    public enum Scope {
        GLOBAL, LOCAL
    }

    private final String name;
    private final Kind kind;
    private final Scope scope;
    private int index = -1;

    private SymbolReference(String name, Kind kind, Scope scope) {
        this.kind = kind;
        this.name = name;
        this.scope = scope;
    }

    public static SymbolReference of(String name, Kind kind, Scope scope) {
        return new SymbolReference(name, kind, scope);
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isGlobal() {
        return scope == Scope.GLOBAL;
    }

    public boolean isLocal() {
        return scope == Scope.LOCAL;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "SymbolReference{" +
                "name='" + name + '\'' +
                ", kind=" + kind +
                ", scope=" + scope +
                ", index=" + index +
                '}';
    }
}
