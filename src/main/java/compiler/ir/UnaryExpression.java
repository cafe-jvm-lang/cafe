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

package compiler.ir;

import java.util.Collections;
import java.util.List;

public class UnaryExpression extends ExpressionStatement<UnaryExpression> {
    private final OperatorType type;
    private ExpressionStatement<?> expressionStatement;

    private UnaryExpression(OperatorType type, ExpressionStatement<?> expressionStatement) {
        this.type = type;
        this.expressionStatement = expressionStatement;
    }

    public static UnaryExpression create(Object type, Object expr) {
        return new UnaryExpression(
                OperatorType.of(type),
                ExpressionStatement.of(expr)
        );
    }

    public OperatorType getType() {
        return type;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Collections.singletonList(expressionStatement);
    }

    @Override
    protected UnaryExpression self() {
        return this;
    }

    @Override
    public String toString() {
        return "UnaryExpression{" +
                type +
                expressionStatement +
                '}';
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitUnaryExpression(this);
    }
}
