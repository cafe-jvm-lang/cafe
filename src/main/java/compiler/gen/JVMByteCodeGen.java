package compiler.gen;

import compiler.cafelang.Temp;
import compiler.cafelang.ir.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.Deque;
import java.util.LinkedList;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class JVMByteCodeGen implements CafeIrVisitor {

    private static final String JOBJECT = "java/lang/Object";
    private static final String TOBJECT = "Ljava/lang/Object;";

    private static final String THIS = "thisPointer";

    private static final String JDYNAMIC = "cafe/DynamicObject";
    private static final String TDYNAMIC = "Lcafe/DynamicObject;";

    private static final String INIT_FUNC_SIGN = "()V";

    private ClassWriter cw;
    private MethodVisitor mv;
    private CafeFunction currentFunction = null;
    private Context context;
    private String className;
    private static final class Context {
        private final Deque<ReferenceTable> referenceTableStack = new LinkedList<>();
    }

    public byte[] generateByteCode(CafeModule module,String className){
        cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        this.context = new Context();
        this.className = className;
        module.accept(this);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private int functionFlags(CafeFunction function) {
        int accessFlags = ACC_STATIC | ACC_PRIVATE ;
        if (function.isSynthetic() ) {
            accessFlags |= ACC_SYNTHETIC;
        }
        if (function.isVarargs()) {
            accessFlags |= ACC_VARARGS;
        }
        return accessFlags;
    }

    private void visitInitFunc(CafeFunction function){
        mv = cw.visitMethod(functionFlags(function),
                function.getName(),
                INIT_FUNC_SIGN,
                null,null);
        mv.visitCode();
        currentFunction = function;

        for(CafeStatement<?> stmt: function.getBlock().getStatements()){
            if(stmt instanceof AssignmentStatement){
                AssignmentStatement s = (AssignmentStatement) stmt;
                if(s.isAssigned() && s.isDeclaring()){

                }
            }
        }

        currentFunction = null;
        mv.visitInsn(RETURN);
        mv.visitMaxs(0,0);
        mv.visitEnd();
    }

    private void visitThisPointer(){
        cw.visitField(ACC_PRIVATE | ACC_STATIC,
                THIS,
                TDYNAMIC,
                null,null)
        .visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC,
                "<clinit>",
                "()V",
                null,null);

        mv.visitTypeInsn(NEW, JDYNAMIC);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, JDYNAMIC, "<init>", "()V", false);
        mv.visitFieldInsn(PUTSTATIC, className, THIS , TDYNAMIC);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    @Override
    public void visitModule(CafeModule module) {
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, JOBJECT, null);
        cw.visitSource(className,null);

        visitThisPointer();
        visitInitFunc(module.getInitFunc());
        module.walk(this);
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

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {

    }

    @Override
    public void visitUnaryExpression(UnaryExpression unaryExpression) {

    }

    @Override
    public void visitConstantStatement(ConstantStatement constantStatement) {

    }
}
