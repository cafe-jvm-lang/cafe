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

import java.util.LinkedList;
import java.util.List;

public class ObjectAccessStatement extends ExpressionStatement<ObjectAccessStatement> {
    private ExpressionStatement<?> accessedOn;
    private ExpressionStatement<?> property;

    public ObjectAccessStatement(ExpressionStatement<?> accessedOn, ExpressionStatement<?> property) {
        this.accessedOn = accessedOn;
        this.property = property;
    }

    public static ObjectAccessStatement create(Object accessedOn, Object property) {
        return new ObjectAccessStatement(
                ExpressionStatement.of(accessedOn),
                ExpressionStatement.of(property)
        );
    }

    public ExpressionStatement<?> getProperty() {
        return property;
    }

    public ExpressionStatement<?> getAccessedOn() {
        return accessedOn;
    }

    @Override
    public List<CafeElement<?>> children() {
        LinkedList<CafeElement<?>> list = new LinkedList<>();
        list.add(accessedOn);
        list.add(property);
        return list;
    }

    @Override
    protected ObjectAccessStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitObjectAccess(this);
    }
}
