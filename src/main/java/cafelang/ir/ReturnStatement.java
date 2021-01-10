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

import java.util.Collections;
import java.util.List;

public class ReturnStatement extends CafeStatement<ReturnStatement> {
    private CafeStatement<?> expressionStatement;
    private boolean isReturningVoid = false;

    private ReturnStatement(ExpressionStatement<?> expression) {
        setExpressionStatement(expression);
    }

    public static ReturnStatement of(Object value) {
        return new ReturnStatement(ExpressionStatement.of(value));
    }

    private void setExpressionStatement(CafeStatement<?> stat) {
        if (stat != null) {
            this.expressionStatement = stat;
        } else {
            this.expressionStatement = null;
        }
    }

    public CafeStatement<?> getExpressionStatement() {
        return expressionStatement;
    }


    @Override
    public List<CafeElement<?>> children() {
        if (expressionStatement != null)
            return Collections.singletonList(expressionStatement);
        return Collections.emptyList();
    }

    @Override
    protected ReturnStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitReturn(this);
    }
}
