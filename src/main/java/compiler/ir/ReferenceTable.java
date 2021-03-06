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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    public ReferenceTable addToTopLevel(SymbolReference reference) {
        if (parent != null)
            parent.addToTopLevel(reference);
        else
            add(reference);
        return this;
    }

    public SymbolReference get(String name) {
        SymbolReference reference = table.get(name);
        if (reference != null)
            return reference;
        if (parent != null)
            return parent.get(name);
        return null;
    }

    public Set<String> getOwnedReferences() {
        return Collections.unmodifiableSet(table.keySet());
    }

    public void relink(ReferenceTable parent) {
        this.parent = parent;
    }

    public ReferenceTable fork() {
        return new ReferenceTable(this);
    }

    public boolean hasReferenceFor(String name) {
        return table.containsKey(name) || parent != null && parent.hasReferenceFor(name);
    }
}
