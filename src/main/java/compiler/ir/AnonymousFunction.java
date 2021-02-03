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

public class AnonymousFunction extends ExpressionStatement<AnonymousFunction> {
    private TargetFuncWrapper targetFuncWrapper;

    private AnonymousFunction(TargetFuncWrapper targetFuncWrapper) {
        this.targetFuncWrapper = targetFuncWrapper;
    }

    public static AnonymousFunction func(TargetFuncWrapper targetFuncWrapper) {
        return new AnonymousFunction(targetFuncWrapper);
    }

    public CafeFunction getFunction() {
        return targetFuncWrapper.getTarget();
    }

    @Override
    public List<CafeElement<?>> children() {
        if (targetFuncWrapper instanceof CafeClosure)
            return Collections.singletonList((CafeClosure) targetFuncWrapper);
        return Collections.singletonList((FunctionWrapper) targetFuncWrapper);
    }

    @Override
    protected AnonymousFunction self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitAnonymousFunction(this);
    }
}
