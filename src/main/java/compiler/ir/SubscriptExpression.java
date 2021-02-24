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

public class SubscriptExpression extends ExpressionStatement<SubscriptExpression> {
    private ExpressionStatement<?> subscriptOf;
    private ExpressionStatement<?> subscriptIndex;

    public SubscriptExpression(ExpressionStatement<?> subscriptOf, ExpressionStatement<?> subscriptIndex) {
        this.subscriptOf = subscriptOf;
        this.subscriptIndex = subscriptIndex;
    }

    public static SubscriptExpression create(Object subscriptOf, Object index) {
        return new SubscriptExpression(
                ExpressionStatement.of(subscriptOf),
                ExpressionStatement.of(index)
        );
    }

    public ExpressionStatement<?> getSubscriptOf() {
        return subscriptOf;
    }

    public ExpressionStatement<?> getSubscriptIndex() {
        return subscriptIndex;
    }

    @Override
    protected SubscriptExpression self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitSubscript(this);
    }
}
