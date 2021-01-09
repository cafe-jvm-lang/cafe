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

package cafelang.ir;

public class ReferenceLookup extends ExpressionStatement<ReferenceLookup> {

    private final String name;

    public ReferenceLookup(String name) {
        this.name = name;
    }

    public static ReferenceLookup of(Object name) {
        if (name instanceof ReferenceLookup) {
            return (ReferenceLookup) name;
        }
        if (name instanceof SymbolReference) {
            return new ReferenceLookup(((SymbolReference) name).getName());
        }
        return new ReferenceLookup(name.toString());
    }

    public SymbolReference resolveIn(ReferenceTable table) {
        return table.get(name);
    }

    public String getName() {
        return name;
    }

    @Override
    protected ReferenceLookup self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitReferenceLookup(this);
    }

    @Override
    public String toString() {
        return "ReferenceLookup{" +
                "name='" + name + '\'' +
                '}';
    }
}
