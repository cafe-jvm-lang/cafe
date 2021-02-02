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

package compiler.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A compilation result.
 */
public class CompilerResult {
    private String moduleName;
    private String sourceFile;
    private byte[] byteCode;
    private Main.Result result;

    private CompilerResult(String moduleName, String sourceFile) {
        this.moduleName = moduleName;
        this.sourceFile = sourceFile;
    }

    public static CompilerResult forModule(String moduleName, String sourceFile) {
        return new CompilerResult(moduleName, sourceFile);
    }

    public CompilerResult error() {
        result = Main.Result.ERROR;
        return this;
    }

    public CompilerResult byteCode(byte[] byteCode) {
        this.byteCode = byteCode;
        result = Main.Result.OK;
        return this;
    }

    public byte[] getByteCode() {
        return byteCode;
    }

    /**
     * Writes JVM bytecode {@code .class} file from the compilation result, in the same directory as the source file.
     * See {@link #writeByteCode(String)} to set the target directory.
     */
    public void writeByteCode() {
        String op = sourceFile.substring(0, sourceFile.lastIndexOf('\\') + 1);
        writeByteCode(op);
    }

    /**
     * Writes JVM bytecode {@code .class} file from the compilation result, in the directory specified.
     *
     * @param outputDir the output target directory.
     */
    public void writeByteCode(String outputDir) {
        write(outputDir);
    }

    private void write(String outputDir) {
        if (!result.isOK())
            return;
        String outputFile = outputDir + moduleName + ".class";
        File file = new File(outputFile);
        write(byteCode, file);
    }

    private void write(byte[] byteCode, File outputFile) {
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            out.write(byteCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOk() {
        return result.isOK();
    }

    public Main.Result getResult() {
        return result;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getSourceFile() {
        return sourceFile;
    }
}
