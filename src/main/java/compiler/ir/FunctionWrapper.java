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

public class FunctionWrapper extends ExpressionStatement<FunctionWrapper> implements TargetFuncWrapper {
    private CafeFunction target;

    private FunctionWrapper(CafeFunction function) {
        this.target = function;
    }

    public static FunctionWrapper wrap(CafeFunction target) {
        return new FunctionWrapper(target);
    }

    @Override
    public CafeFunction getTarget() {
        return target;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Collections.singletonList(target);
    }

    @Override
    protected FunctionWrapper self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunctionWrapper(this);
    }
}
