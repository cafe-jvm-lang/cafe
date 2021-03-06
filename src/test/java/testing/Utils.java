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

package testing;

import runtime.CafeClassLoader;
import runtime.ImportEvaluator;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Utils {

    public static Class<?> compileAndLoad(String filePath) {
        CafeClassLoader loader = new CafeClassLoader();
        return loader.compileAndLoadModule(filePath);
    }

    public static void compileAndStore(String filePath) {
        CafeClassLoader loader = new CafeClassLoader(Utils.class.getClassLoader());
        loader.compileAndStoreModule(filePath, null);
    }

    public static void compileAndStore(String filePath, String destination) {
        CafeClassLoader loader = new CafeClassLoader(Utils.class.getClassLoader());
        loader.compileAndStoreModule(filePath, destination);
    }

    public static Iterator<Object[]> cafeFilesIn(String path) {
        List<Object[]> data = new LinkedList<>();
        File[] files = new File(path).listFiles((dir, name) -> name.endsWith(".cafe"));
        for (File file : files) {
            data.add(new Object[]{file});
        }
        return data.iterator();
    }

    public static void execute(Class<?> clazz) throws Throwable {
        ImportEvaluator evaluator = new ImportEvaluator();
        evaluator.evaluate(clazz);
    }
}
