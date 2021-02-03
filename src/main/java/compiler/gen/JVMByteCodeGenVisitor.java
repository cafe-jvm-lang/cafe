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

package compiler.gen;

import compiler.ir.*;
import library.DFunc;
import library.DObject;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.MethodType;
import java.util.*;

import static compiler.gen.JVMBytecodeUtils.loadInteger;
import static compiler.gen.JVMBytecodeUtils.loadLong;
import static java.lang.invoke.MethodType.genericMethodType;
import static java.lang.invoke.MethodType.methodType;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class JVMByteCodeGenVisitor implements CafeIrVisitor {

    private void printTopOperandStack(MethodVisitor mv) {
        mv.visitInsn(DUP_X1); //duplicat the top value so we only work on the copy
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                "Ljava/io/PrintStream;");//put System.out to operand stack
        mv.visitInsn(SWAP); // swap of the top two values of the opestack: value1 value2 => value2 value1
        // mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V");
    }

    private static final String JOBJECT = "java/lang/Object";
    private static final String TOBJECT = "Ljava/lang/Object;";

    private static final String DOBJECT = "library/DObject";
    private static final String LDOBJECT = "Llibrary/DObject;";

    private static final String JFUNC = "library/DFunc";

    private static final Class<?> DOBJECT_CLASS = DObject.class;
    private static final Class<?> DFUNC_CLASS = DFunc.class;

    private static final String DOBJECT_CREATOR = "runtime/DObjectCreator";

    private static final String INIT_FUNC_SIGN = "()Ljava/util/Map;";
    private static final String INIT_FUNC_TYPE = "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;";

    private static final Handle FUNC_REF_HANDLE = makeHandle(
            "FunctionReferenceID", "Ljava/lang/String;II"
    );

    private static final Handle FUNC_INVOCATION_HANDLE = makeHandle(
            "FunctionInvocationID", "[Ljava/lang/Object;"
    );

    private static final Handle METHOD_INVOCATION_HANDLE = makeHandle(
            "MethodInvocationID", ""
    );

    private static final Handle OBJECT_ACCESS_HANDLE = makeHandle(
            "ObjectAccessID", ""
    );

    private static final Handle OPERATOR_HANDLE = makeHandle(
            "OperatorID", "I"
    );

    private static final Handle IMPORT_HANDLE = makeHandle(
            "ImportID", ""
    );

    private static Handle makeHandle(String methodName, String description) {
        return new Handle(H_INVOKESTATIC,
                "runtime/indy/" + methodName,
                "bootstrap",
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;"
                        + description + ")Ljava/lang/invoke/CallSite;", false);
    }

    private String functionSignature(CafeFunction function) {
        if (function.isInit())
            return INIT_FUNC_SIGN;

        MethodType signature;
        if (function.isVarargs()) {
            signature = MethodType.genericMethodType(function.getArity(), true);
        } else {
            signature = MethodType.genericMethodType(function.getArity() + 1);
        }
        signature = signature.changeParameterType(0, DOBJECT_CLASS);
        return signature.toMethodDescriptorString();
    }

    private String functionType(CafeFunction function) {
        if (function.isInit())
            return INIT_FUNC_TYPE;
        return null;
    }

    private String methodSignature(int arity) {
        return genericMethodType(arity + 1).changeParameterType(0, DOBJECT_CLASS)
                                           .toMethodDescriptorString();
    }

    private ClassWriter cw;
    private MethodVisitor mv;
    private CafeFunction currentFunction = null;
    private Context context;
    private String className;

    private static final class Context {
        private final Deque<ReferenceTable> referenceTableStack = new LinkedList<>();
        private final Map<ForLoopStatement, Label> loopStartMap = new HashMap<>();
        private final Map<ForLoopStatement, Label> loopEndMap = new HashMap<>();
        private final Map<ForLoopStatement, Label> loopIncrMap = new HashMap<>();
        private final Set<String> importedVariables = new HashSet<>();
    }

    private static class GlobalThis {
        private static final String THIS = "#thisPointer";
        private static final String INSERT_THIS = "#insertIntoThis";
        private static final String RETRIEVE_THIS = "#retrieveFromThis";

        private static final String INSERT_THIS_DESC = "(Ljava/lang/String;Ljava/lang/Object;)V";
        private static final String RETRIEVE_THIS_DESC = "(Ljava/lang/String;)Ljava/lang/Object;";

        private static void add(MethodVisitor mv, String className) {
            mv.visitMethodInsn(INVOKESTATIC, className, INSERT_THIS, INSERT_THIS_DESC, false);
        }

        private static void retrieve(MethodVisitor mv, String className) {
            mv.visitMethodInsn(INVOKESTATIC, className, RETRIEVE_THIS, RETRIEVE_THIS_DESC, false);
        }

        private static void declareInsertFunc(ClassWriter cw, String className) {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
                    INSERT_THIS,
                    INSERT_THIS_DESC,
                    null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, className, THIS, LDOBJECT);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, DOBJECT, "define", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static void visitThisPointer(ClassWriter cw, String className) {
            cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
                    THIS,
                    LDOBJECT,
                    null, null)
              .visitEnd();

            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC,
                    "<clinit>",
                    "()V",
                    null, null);

//            mv.visitTypeInsn(NEW, JDYNAMIC);
//            mv.visitInsn(DUP);
//            mv.visitMethodInsn(INVOKESPECIAL, JDYNAMIC, "<init>", "()V", false);
            mv.visitMethodInsn(INVOKESTATIC, DOBJECT_CREATOR, "create", "()" + LDOBJECT, false);
            mv.visitFieldInsn(PUTSTATIC, className, THIS, LDOBJECT);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static void declareRetrieveFunction(ClassWriter cw, String className) {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
                    RETRIEVE_THIS,
                    RETRIEVE_THIS_DESC,
                    null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, className, THIS, LDOBJECT);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, DOBJECT, "get", "(Ljava/lang/String;)Ljava/lang/Object;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        private static void initThis(ClassWriter cw, String className) {
            visitThisPointer(cw, className);
            declareInsertFunc(cw, className);
            declareRetrieveFunction(cw, className);
        }

        private static void loadThis(MethodVisitor mv, String className) {
            mv.visitFieldInsn(GETSTATIC, className, THIS, LDOBJECT);
        }
    }

    public byte[] generateByteCode(CafeModule module, String className) {
        cw = new ClassWriter(COMPUTE_FRAMES | COMPUTE_MAXS);
        this.context = new Context();
        this.className = className;
        writeImportMetaData(module.getImports());
        module.accept(this);
        writeExportMetaData(module.getExports());
        cw.visitEnd();
        return cw.toByteArray();
    }

    private int functionFlags(CafeFunction function) {
        int accessFlags = ACC_STATIC;
        if (function.isInit() || function.isExported())
            accessFlags |= ACC_PUBLIC;
        else
            accessFlags |= ACC_PRIVATE;
        if (function.isSynthetic()) {
            accessFlags |= ACC_SYNTHETIC;
        }
        if (function.isVarargs()) {
            accessFlags |= ACC_VARARGS;
        }
        return accessFlags;
    }

    private void writeMetaData(String name, String[] data) {
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC,
                "#" + name,
                "()[Ljava/lang/String;",
                null, null);
        mv.visitCode();
        loadInteger(mv, data.length);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
        for (int i = 0; i < data.length; i++) {
            mv.visitInsn(DUP);
            loadInteger(mv, i);
            mv.visitLdcInsn(data[i]);
            mv.visitInsn(AASTORE);
        }
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void visitDObjectCreator(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, DOBJECT_CREATOR, "create", "()" + LDOBJECT, false);
    }

    private void writeImportMetaData(Set<CafeImport> imports) {
        // TODO: return import name-alias in #imports function

//        writeMetaData("imports",
//                imports.stream()
//                       .map(CafeImport::getModuleName)
//                       .toArray(String[]::new));

        String refTable = "runtime/ReferenceTable";
        String refSymbol = "runtime/ReferenceSymbol";

        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC,
                "#" + "imports",
                "()L" + refTable + ";",
                null, null);
        mv.visitCode();

        // create an instance of runtime ReferenceTable
        mv.visitTypeInsn(NEW, refTable);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, refTable, "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);

        // loop through each import
        for (CafeImport cafeImport : imports) {
            String path = cafeImport.getModulePath();
            for (Map.Entry<String, String> imp : cafeImport.getNameAlias()
                                                           .entrySet()) {
                // create a new reference symbol
                mv.visitTypeInsn(NEW, refSymbol);
                mv.visitInsn(DUP);
                mv.visitLdcInsn(imp.getKey());

                if (imp.getValue() == null)
                    mv.visitInsn(ACONST_NULL);
                else
                    mv.visitLdcInsn(imp.getValue());
                mv.visitLdcInsn(path);
                mv.visitMethodInsn(INVOKESPECIAL, refSymbol, "<init>",
                        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
                mv.visitVarInsn(ASTORE, 1);

                // add reference symbol to table
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, refTable, "add", "(L" + refSymbol + ";)V", false);
            }
        }
        // return reference table
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    // creates export method
    // public static Map<String, Object> #exports()
    // to return all exports from this module.
    // NOTE: this method should be called always at the end of #init func.
    private void writeExportMetaData(Set<CafeExport> exports) {
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC,
                "#" + "exports",
                "()Ljava/util/Map;",
                "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;",
                null);

        mv.visitCode();

        // create hashmap
        mv.visitTypeInsn(NEW, "java/util/HashMap");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);

        // put each export into map
        for (CafeExport export : exports) {
            mv.visitVarInsn(ALOAD, 0);

            // load key (export name)
            mv.visitLdcInsn(export.getName());

            // load value (export value from GlobalThis pointer)
            mv.visitLdcInsn(export.getName());
            GlobalThis.retrieve(mv, className);

            // map.put(key,value);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);

            // pop boolean returned by map.put
            mv.visitInsn(POP);
        }
        // return map
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    @Override
    public void visitObjectAccess(ObjectAccessStatement objectAccessStatement) {
        objectAccessStatement.walk(this);
    }

    @Override
    public void visitPropertyAccess(PropertyAccess propertyAccess) {
        mv.visitInvokeDynamicInsn(propertyAccess.getName(),
                genericMethodType(1).toMethodDescriptorString(),
                OBJECT_ACCESS_HANDLE
        );
    }

    @Override
    public void visitMethodInvocation(MethodInvocation methodInvocation) {
        ExpressionStatement<?> expr = methodInvocation.getInvokedUpon();

        if (expr instanceof PropertyAccess) {
            mv.visitTypeInsn(CHECKCAST, DOBJECT);
            visitInvocationArguments(methodInvocation.getArguments());
            mv.visitInvokeDynamicInsn(((PropertyAccess) expr).getName(),
                    methodSignature(methodInvocation.getArity()),
                    METHOD_INVOCATION_HANDLE);
            return;
        }

        methodInvocation.walk(this);
        mv.visitTypeInsn(CHECKCAST, JFUNC);
        GlobalThis.loadThis(mv, className);

        MethodType type = genericMethodType(methodInvocation.getArity() + 2).changeParameterType(0, DFUNC_CLASS)
                                                                            .changeParameterType(1,
                                                                                    DOBJECT_CLASS);
        String typedef = type.toMethodDescriptorString();

        visitInvocationArguments(methodInvocation.getArguments());
        mv.visitInvokeDynamicInsn("#_ANNCALL",
                typedef,
                FUNC_INVOCATION_HANDLE);
    }

    @Override
    public void visitSubscript(SubscriptStatement subscriptStatement) {

    }

    @Override
    public void visitFunctionInvocation(FunctionInvocation functionInvocation) {
        MethodType type;

        // This will load Function object (if present) or throw error during runtime.
        functionInvocation.getReference()
                          .accept(this);
        mv.visitTypeInsn(CHECKCAST, JFUNC);

        // load global THIS pointer
        GlobalThis.loadThis(mv, className);

        type = genericMethodType(functionInvocation.getArity() + 2).changeParameterType(0, DFUNC_CLASS)
                                                                   .changeParameterType(1, DOBJECT_CLASS);
        String name = functionInvocation.getName();
        String typedef = type.toMethodDescriptorString();

        visitInvocationArguments(functionInvocation.getArguments());
        mv.visitInvokeDynamicInsn(name, typedef, FUNC_INVOCATION_HANDLE);
    }

    private void visitInvocationArguments(List<CafeElement<?>> arguments) {
        for (CafeElement<?> argument : arguments) {
            argument.accept(this);
        }
    }

    private void visitMainFunc() {
        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, className, "#init", INIT_FUNC_SIGN, false);
        mv.visitInsn(POP);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    @Override
    public void visitModule(CafeModule module) {
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, JOBJECT, null);
        cw.visitSource(className, null);

        GlobalThis.initThis(cw, className);
        visitMainFunc();
        module.walk(this);
    }

    @Override
    public void visitThis(ThisStatement thisStatement) {
        if (thisStatement.isGlobal())
            GlobalThis.loadThis(mv, className);
        else
            mv.visitVarInsn(ALOAD, 0);
    }

    @Override
    public void visitSymbolReference(SymbolReference symbolReference) {

    }

    @Override
    public void visitCafeImport(CafeImport cafeImport) {

    }

    @Override
    public void visitCafeExport(CafeExport cafeExport) {

    }

    @Override
    public void visitBlock(Block block) {
        ReferenceTable table = block.getReferenceTable();
        context.referenceTableStack.push(table);
        for (CafeStatement<?> statement : block.getStatements()) {
            statement.accept(this);
            insertMissingPop(statement);
        }
        context.referenceTableStack.pop();
    }

    private void insertMissingPop(CafeStatement<?> statement) {
        Class<?> statementClass = statement.getClass();
        if (statementClass == FunctionInvocation.class) {
            mv.visitInsn(POP);
        }
    }

    @Override
    public void visitReturn(ReturnStatement returnStatement) {
        CafeStatement<?> statement = returnStatement.getExpressionStatement();
        if (statement != null)
            statement.accept(this);
        else {
            mv.visitInsn(ACONST_NULL);
        }
        mv.visitInsn(ARETURN);
    }

    @Override
    public void visitFunction(CafeFunction cafeFunction) {
        currentFunction = cafeFunction;

        mv = cw.visitMethod(
                functionFlags(cafeFunction),
                cafeFunction.getName(),
                functionSignature(cafeFunction),
                functionType(cafeFunction),
                null
        );

        mv.visitParameter("this", ACC_PRIVATE);
        for (String parameter : cafeFunction.getParameterNames()) {
            mv.visitParameter(parameter, ACC_PRIVATE);
        }

        mv.visitCode();
        cafeFunction.walk(this);

        if (cafeFunction.isInit()) {
            // return export map
            mv.visitMethodInsn(INVOKESTATIC, className, "#exports", "()Ljava/util/Map;", false);
            mv.visitInsn(ARETURN);
        }

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        currentFunction = null;
    }

    @Override
    public void visitClosure(CafeClosure cafeClosure) {
        loadTargetFunction(cafeClosure.getTarget());
        final int closureReferencesSize = cafeClosure.getClosureReferencesSize();

        if (closureReferencesSize > 0) {
            String[] ref = cafeClosure.getClosureReferences()
                                      .toArray(new String[closureReferencesSize]);
            mv.visitInsn(ACONST_NULL);
            loadInteger(mv, closureReferencesSize);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

            for (int i = 0; i < closureReferencesSize; i++) {
                mv.visitInsn(DUP);
                loadInteger(mv, i);
                ReferenceLookup lookup = ReferenceLookup.of(ref[i]);
                lookup.accept(this);
                mv.visitInsn(AASTORE);
            }

            mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "library/DFunc",
                    "bind",
                    "(" + LDOBJECT + "[Ljava/lang/Object;)Llibrary/DFunc;",
                    false
            );
        }
    }

    @Override
    public void visitObjectCreation(ObjectCreationStatement creationStatement) {
        Map<String, ExpressionStatement<?>> map = creationStatement.getMap();
        int index = creationStatement.index();
        // Create DynamicObject instance
        visitDObjectCreator(mv);

//        mv.visitTypeInsn(NEW, JDYNAMIC);
//        mv.visitInsn(DUP);
//        mv.visitMethodInsn(INVOKESPECIAL, JDYNAMIC, "<init>", "()V", false);

        mv.visitVarInsn(ASTORE, index);

        // define every property
        for (Map.Entry<String, ExpressionStatement<?>> entry : map.entrySet()) {
            mv.visitVarInsn(ALOAD, index);
            String key = entry.getKey();
            mv.visitLdcInsn(key);
            entry.getValue()
                 .accept(this);
            mv.visitMethodInsn(INVOKEVIRTUAL, DOBJECT, "define", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
        }
        mv.visitVarInsn(ALOAD, index);
    }

    @Override
    public void visitAssignment(AssignmentStatement assignmentStatement) {
        ExpressionStatement<?> lhs = assignmentStatement.getLhsExpression();
        if (lhs instanceof ReferenceLookup) {
            String name = ((ReferenceLookup) lhs).getName();
            SymbolReference ref = ((ReferenceLookup) lhs).resolveIn(context.referenceTableStack.peek());

            if (ref == null) {
                // check in imports
                if (context.importedVariables.contains(name)) {
                    mv.visitLdcInsn(name);
                    GlobalThis.retrieve(mv, className);
                } else {
                    visitVariableInImports(name);
                }
                return;
            }
            if (ref.isGlobal()) {
                mv.visitLdcInsn(ref.getName());
                assignmentStatement.walk(this);
                GlobalThis.add(mv, className);
            } else {
                assignmentStatement.walk(this);
                mv.visitVarInsn(ASTORE, ref.getIndex());
            }
            return;
        }

        ObjectAccessStatement node;
        if (lhs instanceof ObjectAccessStatement)
            node = (ObjectAccessStatement) lhs;
        else throw new AssertionError("Unknown LHS expression");

        node.getAccessedOn()
            .accept(this);

        ExpressionStatement<?> rhs = assignmentStatement.getRhsExpression();
        rhs.accept(this);
        visitLHSObjectProperty(node.getProperty());

    }

    private void visitLHSObjectProperty(ExpressionStatement<?> expressionStatement) {
        if (expressionStatement instanceof PropertyAccess) {
            mv.visitInvokeDynamicInsn(((PropertyAccess) expressionStatement).getName(),
                    genericMethodType(2).toMethodDescriptorString(),
                    OBJECT_ACCESS_HANDLE
            );
        } else if (expressionStatement instanceof SubscriptStatement) {

        } else {
            // TODO: error
        }
    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        switch (binaryExpression.getType()) {
            case AND:
                andOperator(binaryExpression);
                break;
            case OR:
                orOperator(binaryExpression);
                break;
            default:
                binaryExpression.walk(this);
                genericBinaryOperator(binaryExpression);
        }
    }

    private void genericBinaryOperator(BinaryExpression binaryOperation) {
        String name = binaryOperation.getType()
                                     .name()
                                     .toLowerCase();
        mv.visitInvokeDynamicInsn(name,
                MethodType.genericMethodType(2)
                          .toMethodDescriptorString()
                , OPERATOR_HANDLE, 2);
    }

    private void orOperator(BinaryExpression binaryOperation) {
        Label exitLabel = new Label();
        Label trueLabel = new Label();
        binaryOperation.left()
                       .accept(this);
        asmBooleanValue();
        mv.visitJumpInsn(IFNE, trueLabel);
        binaryOperation.right()
                       .accept(this);
        asmBooleanValue();
        mv.visitJumpInsn(IFNE, trueLabel);
        asmFalseObject();
        mv.visitJumpInsn(GOTO, exitLabel);
        mv.visitLabel(trueLabel);
        asmTrueObject();
        mv.visitLabel(exitLabel);
    }

    private void andOperator(BinaryExpression binaryOperation) {
        Label exitLabel = new Label();
        Label falseLabel = new Label();
        binaryOperation.left()
                       .accept(this);
        asmBooleanValue();
        mv.visitJumpInsn(IFEQ, falseLabel);
        binaryOperation.right()
                       .accept(this);
        asmBooleanValue();
        mv.visitJumpInsn(IFEQ, falseLabel);
        asmTrueObject();
        mv.visitJumpInsn(GOTO, exitLabel);
        mv.visitLabel(falseLabel);
        asmFalseObject();
        mv.visitLabel(exitLabel);
    }

    @Override
    public void visitConditionalBranching(ConditionalBranching conditionalBranching) {
        Label branchingElseLabel = new Label();
        Label branchingExitLabel = new Label();
        conditionalBranching.getCondition()
                            .accept(this);
        asmBooleanValue();
        mv.visitJumpInsn(IFEQ, branchingElseLabel);
        conditionalBranching.getTrueBlock()
                            .accept(this);
        if (conditionalBranching.hasFalseBlock()) {
            if (!conditionalBranching.getTrueBlock()
                                     .hasReturn()) {
                mv.visitJumpInsn(GOTO, branchingExitLabel);
            }
            mv.visitLabel(branchingElseLabel);
            conditionalBranching.getFalseBlock()
                                .accept(this);
            mv.visitLabel(branchingExitLabel);
        } else if (conditionalBranching.hasElseConditionalBranching()) {
            if (!conditionalBranching.getTrueBlock()
                                     .hasReturn()) {
                mv.visitJumpInsn(GOTO, branchingExitLabel);
            }
            mv.visitLabel(branchingElseLabel);
            conditionalBranching.getElseConditionalBranching()
                                .accept(this);
            mv.visitLabel(branchingExitLabel);
        } else {
            mv.visitLabel(branchingElseLabel);
        }
    }

    @Override
    public void visitBreakContinue(BreakContinueStatement breakContinueStatement) {
        Label jumpTarget;
        if (BreakContinueStatement.Type.BREAK.equals(breakContinueStatement.getType())) {
            jumpTarget = context.loopEndMap.get(breakContinueStatement.getEnclosingLoop());
        } else {
            ForLoopStatement loop = breakContinueStatement.getEnclosingLoop();
            jumpTarget = context.loopIncrMap.get(loop);
            if (jumpTarget == null)
                jumpTarget = context.loopStartMap.get(loop);
        }
        mv.visitLdcInsn(0);
        mv.visitJumpInsn(IFEQ, jumpTarget);
    }

    private void asmFalseObject() {
        mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;");
    }

    private void asmTrueObject() {
        mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;");
    }

    private void asmBooleanValue() {
        mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
    }

    @Override
    public void visitUnaryExpression(UnaryExpression unaryExpression) {
        String name = unaryExpression.getType()
                                     .name()
                                     .toLowerCase();
        unaryExpression.walk(this);
        mv.visitInvokeDynamicInsn(name, MethodType.genericMethodType(1)
                                                  .toMethodDescriptorString()
                , OPERATOR_HANDLE, 1);
    }

    @Override
    public void visitConstantStatement(ConstantStatement constantStatement) {
        java.lang.Object value = constantStatement.value();
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
    public void visitReferenceLookup(ReferenceLookup referenceLookup) {
        SymbolReference reference = referenceLookup.resolveIn(context.referenceTableStack.peek());
        String name = referenceLookup.getName();
        // search in imports
        // TODO: optimize searching in imports
        if (reference == null) {
            // if variable was previously imported
//            if (context.importedVariables.contains(name)) {
//                mv.visitLdcInsn(name);
//                GlobalThis.retrieve(mv, className);
//            } else {
//                // create an entry for new imported variable
//                visitVariableInImports(name);
//            }
            visitVariableInImports(name);
            return;
        }

        if (reference.isGlobal()) {
            mv.visitLdcInsn(name);
            GlobalThis.retrieve(mv, className);
        } else {
            mv.visitVarInsn(ALOAD, reference.getIndex());
        }
    }

    private void visitVariableInImports(String varName) {
        // context.importedVariables.add(varName);
        mv.visitLdcInsn(varName);
        mv.visitInvokeDynamicInsn(
                varName,
                genericMethodType(0).toMethodDescriptorString(),
                IMPORT_HANDLE
        );
        mv.visitInsn(DUP_X1);
        GlobalThis.add(mv, className);
    }

    @Override
    public void visitFunctionWrapper(FunctionWrapper functionWrapper) {
        //functionWrapper.walk(this);
        loadTargetFunction(functionWrapper.getTarget());
    }

    private void loadTargetFunction(CafeFunction target) {
        int arity = (target.isVarargs()) ? target.getArity() - 1 : target.getArity() + 1;
        mv.visitInvokeDynamicInsn(
                target.getName(),
                methodType(DFUNC_CLASS).toMethodDescriptorString(),
                FUNC_REF_HANDLE,
                className,
                arity,
                target.isVarargs()
        );
    }

    @Override
    public void visitAnonymousFunction(AnonymousFunction anonymousFunction) {
        anonymousFunction.walk(this);
    }

    @Override
    public void visitDeclarativeAssignment(DeclarativeAssignmentStatement declarativeAssignmentStatement) {
        SymbolReference reference = declarativeAssignmentStatement.getSymbolReference();
        if (reference.isGlobal()) {
            String key = reference.getName();
            mv.visitLdcInsn(key);
            declarativeAssignmentStatement.walk(this);
            GlobalThis.add(mv, className);
        } else {
            declarativeAssignmentStatement.walk(this);
            mv.visitVarInsn(ASTORE, reference.getIndex());
        }
    }

    @Override
    public void visitForLoop(ForLoopStatement forLoopStatement) {
        Label loopStart = new Label();
        Label loopEnd = new Label();
        Label loopIncr = new Label();

        context.loopStartMap.put(forLoopStatement, loopStart);
        context.loopEndMap.put(forLoopStatement, loopEnd);
        context.loopIncrMap.put(forLoopStatement, loopIncr);

        if (forLoopStatement.hasInitStatement())
            for (AssignedStatement init : forLoopStatement.getInitStatements())
                init.accept(this);

        mv.visitLabel(loopStart);
        forLoopStatement.getCondition()
                        .accept(this);
        asmBooleanValue();
        mv.visitJumpInsn(IFEQ, loopEnd);
        forLoopStatement.getBlock()
                        .accept(this);

        mv.visitLabel(loopIncr);
        if (forLoopStatement.hasPostStatement()) {
            for (CafeStatement<?> post : forLoopStatement.getPostStatements())
                post.accept(this);
        }
        mv.visitJumpInsn(GOTO, loopStart);
        mv.visitLabel(loopEnd);
    }

    @Override
    public void visitNull(NullStatement aNull) {
        mv.visitInsn(ACONST_NULL);
    }
}
