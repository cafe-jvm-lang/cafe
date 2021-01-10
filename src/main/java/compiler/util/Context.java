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

package compiler.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Context ensures that single context is used for each compiler phase invocation.
 * <p>Every phase registers itself with this context</p>
 *
 * @author Dhyey
 */
public class Context {
    public static class Key<T> {
    }

    protected final Map<Key<?>, Object> map = new HashMap<>();

    public <T> void put(Key<T> key, T fac) {
        Object old = map.put(key, fac);
        if (old != null) {
            throw new AssertionError("duplicate value");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        Object o = map.get(key);
        return (T) o;
    }
}
