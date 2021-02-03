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

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class AnnFuncNameGeneratorTest {

    @Test
    public static void function_stack() {
        AnnFuncNameGenerator gen = new AnnFuncNameGenerator();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_"));

        gen.enter();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_1"));

        gen.next();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_2"));

        gen.enter();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_2_3"));

        gen.next();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_2_4"));

        gen.leave();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_2"));

        gen.next();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_5"));

        gen.leave();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_"));

        gen.leave();
        assertTrue(gen.current()
                      .equals("#_ANN_FUNC_"));
    }
}
