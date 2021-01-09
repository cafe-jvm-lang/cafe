/*
 * Copyright (c) 2021. Dhyey Shah <dhyeyshah4@gmail.com>
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

import java.util.Arrays;
import java.util.List;

public class BinaryExpression extends ExpressionStatement<BinaryExpression> {
    private final OperatorType type;
    private ExpressionStatement<?> leftExpression;
    private ExpressionStatement<?> rightExpression;

    private BinaryExpression(OperatorType type) {
        this.type = type;
    }

    @Override
    protected BinaryExpression self() {
        return this;
    }

    public static BinaryExpression of(Object type) {
        return new BinaryExpression(OperatorType.of(type));
    }

    public BinaryExpression left(Object expr) {
        this.leftExpression = ExpressionStatement.of(expr);
        return this;
    }

    public BinaryExpression right(Object expr) {
        this.rightExpression = ExpressionStatement.of(expr);
        return this;
    }

    public ExpressionStatement<?> right() {
        return rightExpression;
    }

    public ExpressionStatement<?> left() {
        return leftExpression;
    }

    public OperatorType getType() {
        return type;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Arrays.asList(leftExpression, rightExpression);
    }

    @Override
    public String toString() {
        return "BinaryExpression{" +
                "type=" + type +
                ", leftExpression=" + leftExpression +
                ", rightExpression=" + rightExpression +
                '}';
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }
}
