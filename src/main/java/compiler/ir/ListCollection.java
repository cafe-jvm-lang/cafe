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

public class ListCollection extends ExpressionStatement<ListCollection> {
    private List<ExpressionStatement<?>> items = new LinkedList<>();
    private int index = -1;

    private ListCollection() {
    }

    public static ListCollection list() {
        return new ListCollection();
    }

    public void add(ExpressionStatement<?> expr) {
        items.add(expr);
    }

    public List<ExpressionStatement<?>> getItems() {
        return items;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

    @Override
    public List<CafeElement<?>> children() {
        return new LinkedList<>(items);
    }

    @Override
    protected ListCollection self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitListCollection(this);
    }
}
