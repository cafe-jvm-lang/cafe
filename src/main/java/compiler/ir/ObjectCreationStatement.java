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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ObjectCreationStatement extends ExpressionStatement<ObjectCreationStatement> {
    private int index = -1;
    private Map<String, ExpressionStatement<?>> map;

    private ObjectCreationStatement(Map<String, ExpressionStatement<?>> map) {
        this.map = map;
    }

    public static ObjectCreationStatement of(Map<String, ExpressionStatement<?>> map) {
        return new ObjectCreationStatement(map);
    }

    public Map<String, ExpressionStatement<?>> getMap() {
        return map;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    @Override
    public List<CafeElement<?>> children() {
        return new LinkedList<>(map.values());
    }

    @Override
    protected ObjectCreationStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitObjectCreation(this);
    }
}
