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

package runtime;

import runtime.imports.ModulePath;

import java.util.*;

public final class ReferenceTable {
    private List<ReferenceSymbol> symbols = new LinkedList<>();

    public ReferenceTable() {
    }

    public void add(ReferenceSymbol symbol) {
        symbols.add(symbol);
    }

    public Set<ModulePath> getImportPaths() {
        Set<ModulePath> list = new HashSet<>();
        for (ReferenceSymbol symbol : symbols) {
            try {
                list.add(ModulePath.fromPath(symbol.getPath()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public ReferenceSymbol resolve(String importName) {
        Optional<ReferenceSymbol> op = symbols.stream()
                                              .filter(e -> e.getAlias() == importName)
                                              .findFirst();
        if (op.isPresent()) {
            return op.get();
        }
        op = symbols.stream()
                    .filter(e -> e.getName() == importName)
                    .findFirst();
        if (op.isPresent()) {
            return op.get();
        }
        return null;
    }

}
