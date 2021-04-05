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

import java.util.LinkedList;
import java.util.List;

public class MethodInvocation extends ExpressionStatement<MethodInvocation> {
    private ExpressionStatement<?> invokedUpon;
    private List<CafeElement<?>> arguments;

    private MethodInvocation(ExpressionStatement<?> invokedUpon, List<CafeElement<?>> arguments) {
        this.invokedUpon = invokedUpon;
        this.arguments = arguments;
    }

    public static MethodInvocation create(Object invokedOn, List<ExpressionStatement<?>> args) {
        List<CafeElement<?>> arguments = new LinkedList<>();
        for (Object arg : args) {
            if (arg instanceof CafeElement) {
                arguments.add((CafeElement) arg);
            } else {
                arguments.add(ExpressionStatement.of(arg));
            }
        }
        return new MethodInvocation(
                ExpressionStatement.of(invokedOn),
                arguments
        );
    }

    public List<CafeElement<?>> getArguments() {
        return arguments;
    }

    public int getArity() {
        return arguments.size();
    }

    public ExpressionStatement<?> getInvokedUpon() {
        return invokedUpon;
    }

//    @Override
//    public List<CafeElement<?>> children() {
//        return Collections.singletonList(invokedUpon);
//    }

    @Override
    protected MethodInvocation self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitMethodInvocation(this);
    }

    @Override
    public String toString() {
        return "MethodInvocation{" +
                "name=" + invokedUpon +
                "(...)}";
    }
}
