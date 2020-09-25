package compiler.gen;

import compiler.cafelang.ir.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class JVMByteCodeGen implements CafeIrVisitor {

    private ClassWriter cw;
    private MethodVisitor mv;

    @Override
    public void visitModule(CafeModule module) {
        cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        module.accept(this);
        cw.visitEnd();
    }

    @Override
    public void visitSymbolReference(SymbolReference symbolReference) {

    }

    @Override
    public void visitBlock(Block block) {

    }

    @Override
    public void visitFunction(CafeFunction cafeFunction) {

    }

    @Override
    public void visitAssignment(AssignmentStatement assignmentStatement) {

    }
}
