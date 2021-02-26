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

package library;

import runtime.DObjectCreator;

import java.util.ArrayList;
import java.util.List;

public class DList extends DObject implements Subscriptable, Slicable {
    private List<Object> list;

    public DList(DObject __proto__) {
        super(__proto__);
        list = new ArrayList<>();
    }

    public void add(Object object) {
        list.add(object);
    }

    public void remove(Object object) {
        list.remove(object);
    }

    public void removeAt(int index) {
        list.remove(index);
    }

    public Object get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    @Override
    public Object getSubscript(Object key) {
        return get((Integer) key);
    }

    @Override
    public void setSubscript(Object key, Object value) {
        list.set((Integer) key, value);
    }

    @Override
    public List<Object> slice(int s, int e) {
        return new ArrayList<>(list.subList(s, e));
    }

    @Override
    public void setSlice(int s, int e, Object value) {
        DList o = DObjectCreator.createList();
        if (value instanceof DList)
            o = (DList) value;
        else
            o.add(value);
        int i, j, size = o.size();
        for (i = s, j = 0; i < e && j < size; i++, j++) {
            list.set(i, o.get(j));
        }
        while (j < o.size()) {
            list.add(o.get(j++));
        }

        while (i < e) {
            list.remove(i);
            e--;
        }
    }

    @Override
    public String toString() {
        return "DList{" +
                "list=" + list +
                '}';
    }
}
