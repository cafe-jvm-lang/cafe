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

package compiler.parser;

import compiler.ir.CafeModule;
import compiler.main.SourceFileManager;
import compiler.util.Context;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import testing.Utils;

import java.io.File;
import java.util.Iterator;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class ASTParserTest {
    public static final String SRC = "src/test/resources/parse-test/";
    public static final String ERR = "src/test/resources/parse-test/error_test";

    private ParserFactory factory;
    private SourceFileManager fm;

    @DataProvider(name = "cafe-files")
    public static Iterator<Object[]> data() {
        return Utils.cafeFilesIn(SRC);
    }

    @DataProvider(name = "cafe-error-files")
    public static Iterator<Object[]> error_data() {
        return Utils.cafeFilesIn(ERR);
    }

    @BeforeMethod
    public void init() {
        Context context = new Context();
        factory = ParserFactory.instance(context);
        fm = SourceFileManager.instance(context);
    }

    @Test(dataProvider = "cafe-files")
    public void test_no_error(File file) {
        fm.setSourceFile(file);
        Parser parser = factory.newParser(ParserType.IRParser, fm.asCharList());
        CafeModule module = parser.parseToIR(file.getName());
        System.out.println("Success "+module);
//        Node n = parser.parse();
        assertNotNull(module);
    }

    @Test(dataProvider = "cafe-error-files")
    public void test_error(File file) {
        fm.setSourceFile(file);
        Parser parser = factory.newParser(ParserType.IRParser, fm.asCharList());
        CafeModule module = parser.parseToIR(file.getName());
        System.out.println("Success "+module);
//        Node n = parser.parse();
        assertNull(module);
    }
}
