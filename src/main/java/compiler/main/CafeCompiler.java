package compiler.main;

import cafelang.ir.CafeModule;
import compiler.analyzer.SemanticsChecker;
import compiler.ast.Node;
import compiler.ast.Node.ProgramNode;
import compiler.gen.ASTToCafeIrVisitor;
import compiler.gen.JVMByteCodeGenVisitor;
import compiler.gen.SymbolReferenceAssignmentVisitor;
import compiler.main.Main.Result;
import compiler.parser.Parser;
import compiler.parser.ParserFactory;
import compiler.parser.ParserType;
import compiler.util.Context;
import compiler.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static compiler.util.Messages.success;

public class CafeCompiler {
    private ParserFactory parserFactory;
    private Log log;
    private SourceFileManager fileManager;

    private Parser parser;
    private SemanticsChecker analyzer;

    private final String source;
    private String outputFilePath;
    private final String moduleName;

    public CafeCompiler(String source) {
        Context context = new Context();
        this.source = source;

        log = Log.instance(context);
        fileManager = SourceFileManager.instance(context);

        String fileName = source.substring( source.lastIndexOf('\\')+1);
        moduleName = fileName.substring(0, fileName.lastIndexOf('.'));

        outputFilePath = source.substring( 0, source.lastIndexOf('\\')+1);
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

    public Result compile() {
        Node programNode = null;
        CafeModule module = null;
        byte[] byteCode = null;
        for (Phase phase : Phase.values()) {
            switch (phase) {
                case INIT:
                    fileManager.setSourceFile(source);
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
                    module.accept(new SymbolReferenceAssignmentVisitor());
                    break;
                case GEN:
                    byteCode = new JVMByteCodeGenVisitor().generateByteCode(module, moduleName);
                    File op = new File(outputFilePath);
                    try (FileOutputStream out = new FileOutputStream(op)) {
                        out.write(byteCode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            if (checkErrors()) {
                return Result.ERROR;
            }
        }
        success("");
        return Result.OK;
    }
}
