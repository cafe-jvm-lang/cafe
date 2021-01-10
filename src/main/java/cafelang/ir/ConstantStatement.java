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

package cafelang.ir;

public class ConstantStatement extends ExpressionStatement<ConstantStatement> {
    private Object value;

    public ConstantStatement(Object o) {
        this.value = o;
    }

    @Override
    protected ConstantStatement self() {
        return this;
    }

    public static ConstantStatement of(Object o) {
        if (o instanceof ConstantStatement)
            return (ConstantStatement) o;

        if (!isLiteralValue(o)) {
            throw new IllegalArgumentException("Not a constant value: " + o);
        }
        return new ConstantStatement(o);
    }

    public static boolean isLiteralValue(Object v) {
        return v == null
                || v instanceof String
                || v instanceof Character
                || v instanceof Number
                || v instanceof Boolean
                ;
    }

    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return "Const{" +
                value +
                '}';
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitConstantStatement(this);
    }
}
