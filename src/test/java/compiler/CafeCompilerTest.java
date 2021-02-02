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

import compiler.main.CafeCompiler;
import compiler.main.CompilerResult;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import testing.Utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

public class CafeCompilerTest {
    public static final String SRC = "src/test/resources/compilation/";

    @DataProvider(name = "cafe-files")
    public static Iterator<Object[]> data() {
        return Utils.cafeFilesIn(SRC);
    }

    @Test(dataProvider = "cafe-files")
    public void generate_bytecode(File cafeFile) {
        CafeCompiler compiler = new CafeCompiler(cafeFile.getAbsolutePath());
        CompilerResult result = compiler.compile();

        ClassReader reader = new ClassReader(result.getByteCode());
        TraceClassVisitor tracer = new TraceClassVisitor(new PrintWriter(System.out));
        reader.accept(tracer, 0);

        assertEquals(result.getByteCode().length > 0, true);
    }
}
