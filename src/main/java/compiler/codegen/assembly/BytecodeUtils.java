package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.MethodVisitor;

final class BytecodeUtils {
	static int[] ICONST;
	static {
		ICONST = new int[] { ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 };
	}

	static void loadLiteralToOperandStack(MethodVisitor mv, Object value) {
		if (value == null) {
			mv.visitInsn(ACONST_NULL);
			return;
		}
		if (value instanceof Integer) {
			Integer i = Integer.valueOf((Integer) value);
			if (i > Short.MIN_VALUE && i < Short.MAX_VALUE) {
				if (i > Byte.MIN_VALUE && i < Byte.MAX_VALUE) {
					if (i > -1 && i < 6) {
						mv.visitInsn(ICONST[i]);
					} else {
						mv.visitIntInsn(BIPUSH, i);
					}
				} else {
					mv.visitIntInsn(SIPUSH, i);
				}
			} else {
				mv.visitLdcInsn(value);
			}
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			return;
		}
	}

	static void loadVariableToOperandStack(MethodVisitor mv, int var) {
		mv.visitVarInsn(ALOAD, var);
	}

	static void storeVariableFromOperandStack(MethodVisitor mv, int var) {
		mv.visitVarInsn(ASTORE, var);
	}
}
