package compiler.codegen.assembly;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.MethodVisitor;

final class BytecodeUtils {
	static int[] ICONST;
	static {
		ICONST = new int[] {
				ICONST_0,ICONST_1,ICONST_2,ICONST_3,ICONST_4,ICONST_5
		};
	}
	
	static void loadToOperandStack(MethodVisitor mv, Object value) {
		if( value == null) {
			mv.visitInsn(ACONST_NULL);
			return;
		}
		if( value instanceof Integer) {
			int i = (Integer) value;
			
			if(i>-1 && i<6) {
				mv.visitInsn(ICONST[i]);
				
			}
			else {
				mv.visitIntInsn(BIPUSH, i);
			}
			return;
		}
	}
}
