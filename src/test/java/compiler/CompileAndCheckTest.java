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

package compiler;

import library.DObject;
import org.testng.annotations.Test;
import runtime.DObjectCreator;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static testing.Utils.*;

public class CompileAndCheckTest {

    public static final String SRC = "src/test/resources/";
    public static final DObject thisP = DObjectCreator.create();

    @Test
    public void test_ann_func() throws Throwable {
        String folder = SRC + "ann-func-test/";
        compileAndStore(folder + "anonymous_func_export.cafe");

        Class<?> clazz = compileAndLoad(folder + "anonymous_func_import.cafe");
        execute(clazz);

        //Arrays.stream(clazz.getDeclaredMethods()).forEach(System.out::println);
        Method method1 = clazz.getMethod("test", DObject.class);
        assertEquals(method1.invoke(null, thisP), "Hello");
    }

    @Test
    public void test_ann_func_as_export() throws Throwable {
        String folder = SRC + "ann-func-test/as-closure/";
        compileAndStore(folder + "ann_func_export.cafe");

        Class<?> clazz = compileAndLoad(folder + "ann_func_import.cafe");
        execute(clazz);

        Method method1 = clazz.getMethod("test", DObject.class);
        assertEquals(method1.invoke(null, thisP), "inside closure");
    }
}
