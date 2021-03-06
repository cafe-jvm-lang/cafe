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

package library.base;

import library.DList;
import library.DObject;

public class CListProto {
    public static void add(DObject object, Object value) {
        ((DList) object).add(value);
    }

    public static Object get(DObject object, Object index) {
        return ((DList) object).get((Integer) index);
    }

    public static void remove(DObject object, Object value) {
        ((DList) object).remove(value);
    }

    public static void removeAt(DObject object, Object value) {
        ((DList) object).removeAt((Integer) value);
    }

    public static int size(DObject object) {
        return ((DList) object).size();
    }
}
