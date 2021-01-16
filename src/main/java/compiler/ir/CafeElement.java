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
import java.util.List;

public abstract class CafeElement<T extends CafeElement<T>> {
    private CafeElement<?> parent;

    protected abstract T self();

    private void setParent(CafeElement<?> parent) {
        this.parent = parent;
    }

    public final CafeElement<?> parent() {
        return this.parent;
    }

    public abstract void accept(CafeIrVisitor visitor);

    public List<CafeElement<?>> children() {
        return Collections.emptyList();
    }

    protected static final RuntimeException cantConvert(String expected, Object value) {
        return new ClassCastException(String.format(
                "expecting a %s but got a %s",
                expected,
                value == null ? "null value" : value.getClass()
                                                    .getName()));
    }

    public void walk(CafeIrVisitor visitor) {
        for (CafeElement<?> e : children())
            e.accept(visitor);
    }
}
