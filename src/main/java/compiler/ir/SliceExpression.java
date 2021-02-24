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

public class SliceExpression extends ExpressionStatement<SliceExpression> {

    private ExpressionStatement<?> slicedOn;
    private ExpressionStatement<?> beginIndex;
    private ExpressionStatement<?> endIndex;

    private SliceExpression(ExpressionStatement<?> slicedOn) {
        this.slicedOn = slicedOn;
    }

    public static SliceExpression slice(Object slicedOn) {
        return new SliceExpression(ExpressionStatement.of(slicedOn));
    }

    public SliceExpression beginsAt(Object beginIndex) {
        this.beginIndex = ExpressionStatement.of(beginIndex);
        return this;
    }

    public SliceExpression endsAt(Object endIndex) {
        this.endIndex = ExpressionStatement.of(endIndex);
        return this;
    }

    public SliceExpression range(Object beginIndex, Object endIndex) {
        this.beginIndex = ExpressionStatement.of(beginIndex);
        this.endIndex = ExpressionStatement.of(endIndex);
        return this;
    }

    public ExpressionStatement<?> getBeginIndex() {
        return beginIndex;
    }

    public ExpressionStatement<?> getEndIndex() {
        return endIndex;
    }

    public ExpressionStatement<?> getSlicedOn() {
        return slicedOn;
    }

    @Override
    protected SliceExpression self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitSlice(this);
    }
}
