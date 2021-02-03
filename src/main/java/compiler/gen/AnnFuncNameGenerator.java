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

package compiler.gen;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Generates name for anonymous functions.
 * <p>
 * Generated name follows the pattern {@code #_ANN_FUNC_<counter>_<counter>}
 * <p>
 * Example:
 * <code>
 * AnnFuncNameGenerator gen = new AnnFuncNameGenerator();
 * assertTrue(gen.current().equals("#_ANN_FUNC_"));
 * <p>
 * gen.enter();
 * assertTrue(gen.current().equals("#_ANN_FUNC_1"));
 * <p>
 * gen.next();
 * assertTrue(gen.current().equals("#_ANN_FUNC_2"));
 * <p>
 * gen.enter();
 * assertTrue(gen.current().equals("#_ANN_FUNC_2_3"));
 * <p>
 * gen.next();
 * assertTrue(gen.current().equals("#_ANN_FUNC_2_4"));
 * <p>
 * gen.leave();
 * assertTrue(gen.current().equals("#_ANN_FUNC_2"));
 * <p>
 * gen.next();
 * assertTrue(gen.current().equals("#_ANN_FUNC_5"));
 * <p>
 * gen.leave();
 * assertTrue(gen.current().equals("#_ANN_FUNC_"));
 * <p>
 * gen.leave();
 * assertTrue(gen.current().equals("#_ANN_FUNC_"));
 *
 * </code>
 * </p>
 * </p>
 */
public class AnnFuncNameGenerator {
    private int level = 0;

    private final String PREFIX = "#_ANN_FUNC_";
    private final String SEP = "_";

    private int counter = 0;
    private Deque<Integer> prefixes = new LinkedList<>();

    public AnnFuncNameGenerator() {

    }

    public AnnFuncNameGenerator enter() {
        level++;
        prefixes.addLast(++counter);
        return this;
    }

    public AnnFuncNameGenerator leave() {
        level--;
        if (level < 0)
            level = 0;
        if (!prefixes.isEmpty()) {
            prefixes.removeLast();
        }

        return this;
    }

    public int depth() {
        return level;
    }

    public String next() {
        prefixes.removeLast();
        prefixes.addLast(++counter);
        return getAnnFuncName();
    }

    public String current() {
        return getAnnFuncName();
    }

    private String getAnnFuncName() {
        String name = PREFIX + prefixes.stream()
                                       .map(String::valueOf)
                                       .collect(Collectors.joining(SEP));
        return name;
    }
}
