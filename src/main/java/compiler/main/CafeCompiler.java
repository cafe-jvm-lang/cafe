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

import compiler.analyzer.SemanticsChecker;
import compiler.ast.Node;
import compiler.ast.Node.ProgramNode;
import compiler.gen.ASTToCafeIrVisitor;
import compiler.gen.ClosureReferenceVisitor;
import compiler.gen.JVMByteCodeGenVisitor;
import compiler.gen.SymbolReferenceAssignmentVisitor;
import compiler.ir.CafeModule;
import compiler.parser.Parser;
import compiler.parser.ParserFactory;
import compiler.parser.ParserType;
import compiler.util.Context;
import compiler.util.Log;

import static compiler.util.Messages.success;

/**
 * The Cafe Compiler.
 * <p> An instance of this class compiles one Cafe source file. Multiple instances need to be created to compile multiple Cafe sources. </p>
 */
public class CafeCompiler {
    private ParserFactory parserFactory;
    private Log log;
    private SourceFileManager fileManager;

    private Parser parser;
    private SemanticsChecker analyzer;

    private final String sourceFile;
    private String outputFilePath;
    private final String moduleName;

    /**
     * Instantiates this class by specifying the Cafe source to be compiled.
     *
     * @param sourceFile the cafe source file to be compiled.
     */
    public CafeCompiler(String sourceFile) {
        Context context = new Context();
        this.sourceFile = sourceFile;

        log = Log.instance(context);
        fileManager = SourceFileManager.instance(context);

        String fileName = sourceFile.substring(sourceFile.lastIndexOf('\\') + 1);
        moduleName = fileName.substring(0, fileName.lastIndexOf('.'));

        outputFilePath = sourceFile.substring(0, sourceFile.lastIndexOf('\\') + 1);
        outputFilePath += moduleName + ".class";

        parserFactory = ParserFactory.instance(context);

        analyzer = SemanticsChecker.instance(context);
    }

    enum Phase {
        INIT,
        PARSE,
        ANALYZE,
        IR,
        GEN
    }

    boolean checkErrors() {
        if (log.entries() > 0) {
            log.printIssues();
            return true;
        }
        return false;
    }

    /**
     * Compiles the Cafe source file specified during instantiation.
     *
     * @return a compilation result.
     */
    public CompilerResult compile() {
        Node programNode = null;
        CafeModule module = null;
        byte[] byteCode;
        CompilerResult result = CompilerResult.forModule(moduleName, sourceFile);
        for (Phase phase : Phase.values()) {
            switch (phase) {
                case INIT:
                    fileManager.setSourceFile(sourceFile);
                    break;
                case PARSE:
                    parser = parserFactory.newParser(ParserType.MAINPARSER, fileManager.asCharList());
                    programNode = parser.parse();
                    break;
                case ANALYZE:
//                    System.out.println((char) 27 + "[33m" + "\nPrettyPrint");
//                    new PrettyPrinter().prettyPrint(programNode);
                    analyzer.visitProgram((ProgramNode) programNode);
                    break;
                case IR:
                    module = new ASTToCafeIrVisitor().transform((ProgramNode) programNode, moduleName);
                    module.accept(new ClosureReferenceVisitor());
                    module.accept(new SymbolReferenceAssignmentVisitor());
                    break;
                case GEN:
                    byteCode = new JVMByteCodeGenVisitor().generateByteCode(module, moduleName);
                    result.ok(byteCode);
            }
            if (checkErrors()) {
                return result.error();
            }
        }
        success("");
        return result;
    }
}
