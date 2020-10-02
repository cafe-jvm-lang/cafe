package compiler.gen;

import compiler.cafelang.ir.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.Deque;
import java.util.LinkedList;

import static compiler.gen.JVMBytecodeUtils.loadInteger;
import static compiler.gen.JVMBytecodeUtils.loadLong;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class JVMByteCodeGen implements CafeIrVisitor {

    private static final String JOBJECT = "java/lang/Object";
    private static final String TOBJECT = "Ljava/lang/Object;";

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

    private static class This{
        private static final String THIS = "#thisPointer";
        private static final String INSERT_THIS = "#insertIntoThis";
        private static final String RETRIEVE_THIS = "#retrieveFromThis";

        private static final String INSERT_THIS_DESC = "(Ljava/lang/String;Ljava/lang/Object;)V";
        private static final String RETRIEVE_THIS_DESC = "(Ljava/lang/String;)Ljava/lang/Object;";

        private static void invokeInsertThis(MethodVisitor mv,String className){
            mv.visitMethodInsn(INVOKESTATIC, className, INSERT_THIS, INSERT_THIS_DESC, false);
        }

        private static void visitInsertIntoThisFunc(ClassWriter cw, String className){
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC,
                    INSERT_THIS,
                    INSERT_THIS_DESC,
                    null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC,className,THIS,TDYNAMIC);
            mv.visitVarInsn(ALOAD,0);
            mv.visitVarInsn(ALOAD,1);
            mv.visitMethodInsn(INVOKEVIRTUAL, JDYNAMIC, "define", "(Ljava/lang/String;Ljava/lang/Object;)V",false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static void visitThisPointer(ClassWriter cw, String className){
            cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
                    THIS,
                    TDYNAMIC,
                    null,null)
                    .visitEnd();

            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC,
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

        private static void visitRetrieveFromThisFunc(ClassWriter cw, String className){
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC,
                    RETRIEVE_THIS,
                    RETRIEVE_THIS_DESC,
                    null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC,className,THIS,TDYNAMIC);
            mv.visitVarInsn(ALOAD,0);
            mv.visitMethodInsn(INVOKEVIRTUAL, JDYNAMIC, "get", "(Ljava/lang/String;)Ljava/lang/Object;",false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static void loadThis(ClassWriter cw, String className){
            visitThisPointer(cw,className);
            visitInsertIntoThisFunc(cw,className);
            visitRetrieveFromThisFunc(cw,className);
        }
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

    @Override
    public void visitObjectAccess(ObjectAccessStatement objectAccessStatement) {

    }

    @Override
    public void visitMethodInvoke(MethodInvocation methodInvocation) {

    }

    @Override
    public void visitSubscript(SubscriptStatement subscriptStatement) {

    }

    @Override
    public void visitPropertyAccess(PropertyAccess propertyAccess) {

    }

    @Override
    public void visitFunctionInvoke(FunctionInvocation functionInvocation) {

    }

    private void visitInitFunc(CafeFunction function) {
        mv = cw.visitMethod(functionFlags(function),
                function.getName(),
                INIT_FUNC_SIGN,
                null, null);
        mv.visitCode();
        currentFunction = function;

        for (CafeStatement<?> stmt : function.getBlock().getStatements()) {
            if (stmt instanceof DeclarativeAssignmentStatement) {
                DeclarativeAssignmentStatement s = (DeclarativeAssignmentStatement) stmt;
                String key = s.getSymbolReference().getName();
                mv.visitLdcInsn(key);
                s.walk(this);
                This.invokeInsertThis(mv,className);
            }
        }

        currentFunction = null;
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void visitMainFunc(){
        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, className, "#init", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    @Override
    public void visitModule(CafeModule module) {
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, JOBJECT, null);
        cw.visitSource(className,null);

        This.loadThis(cw,className);
        visitInitFunc(module.getInitFunc());
        visitMainFunc();
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
        Object value = constantStatement.value();
        if (value instanceof Integer) {
            int i = (Integer) value;
            loadInteger(mv, i);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            return;
        }
        if (value instanceof Long) {
            long l = (Long) value;
            loadLong(mv, l);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            return;
        }
        if (value instanceof Boolean) {
            boolean b = (Boolean) value;
            loadInteger(mv, b ? 1 : 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            return;
        }
        if (value instanceof String) {
            mv.visitLdcInsn(value);
            return;
        }
        if (value instanceof Double) {
            double d = (Double) value;
            mv.visitLdcInsn(d);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            return;
        }
        if (value instanceof Float) {
            float f = (Float) value;
            mv.visitLdcInsn(f);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            return;
        }
        throw new IllegalArgumentException("Constants of type " + value.getClass() + " cannot be handled.");
    }

    @Override
    public void visitRefereceLookup(ReferenceLookup referenceLookup) {

    }

    @Override
    public void visitFunctionReference(FunctionReference functionReference) {

    }

    @Override
    public void visitDeclarativeAssignment(DeclarativeAssignmentStatement declarativeAssignmentStatement) {

    }

    @Override
    public void visitNull(NullStatement aNull) {
        mv.visitInsn(ACONST_NULL);
    }
}
