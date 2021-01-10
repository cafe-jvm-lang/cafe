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

package cafelang.ir;

import java.util.Objects;

public class CafeImport extends CafeElement<CafeImport> {
    private final String functions;
    private final String moduleName;

    private CafeImport(String functions, String moduleName) {
        this.functions = functions;
        this.moduleName = moduleName;
    }

    public static CafeImport of(String functions, String moduleName) {
        return new CafeImport(functions, moduleName);
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    protected CafeImport self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitCafeImport(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeImport that = (CafeImport) o;
        return functions.equals(that.functions) &&
                moduleName.equals(that.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functions, moduleName);
    }
}
