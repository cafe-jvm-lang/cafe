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

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class JVMBytecodeUtils {
    private JVMBytecodeUtils() {
    }

    static boolean between(int value, int lower, int upper) {
        return (value >= lower) && (value <= upper);
    }

    private static final int[] ICONST = {ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5};

    static void loadInteger(MethodVisitor methodVisitor, int value) {
        if (between(value, Short.MIN_VALUE, Short.MAX_VALUE)) {
            if (between(value, Byte.MIN_VALUE, Byte.MAX_VALUE)) {
                if (between(value, -1, 5)) {
                    methodVisitor.visitInsn(ICONST[value + 1]);
                } else {
                    methodVisitor.visitIntInsn(BIPUSH, value);
                }
            } else {
                methodVisitor.visitIntInsn(SIPUSH, value);
            }
        } else {
            methodVisitor.visitLdcInsn(value);
        }
    }

    static void loadLong(MethodVisitor methodVisitor, long value) {
        if (value == 0) {
            methodVisitor.visitInsn(LCONST_0);
        } else if (value == 1) {
            methodVisitor.visitInsn(LCONST_1);
        } else {
            methodVisitor.visitLdcInsn(value);
        }
    }
}
