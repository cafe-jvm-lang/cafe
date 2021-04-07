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

public class FunctionInvocation extends ExpressionStatement<FunctionInvocation> {
    private String name;
    private ExpressionStatement<?> ref;
    private List<CafeElement<?>> arguments;

    private FunctionInvocation(String name, ExpressionStatement<?> ref, List<CafeElement<?>> arguments) {
        this.name = name;
        this.ref = ref;
        this.arguments = arguments;
    }

    public ExpressionStatement<?> getReference() {
        return ref;
    }

    public static FunctionInvocation create(Object ref, List<ExpressionStatement<?>> args) {
        List<CafeElement<?>> arguments = new LinkedList<>();
        for (Object arg : args) {
            if (arg instanceof CafeElement<?>) {
                arguments.add((CafeElement<?>) arg);
            } else {
                arguments.add(ExpressionStatement.of(arg));
            }
        }
        String name = "#_ANN_CALL";
        if (ref instanceof ReferenceLookup)
            name = ((ReferenceLookup) ref).getName();
        return new FunctionInvocation(
                name,
                ExpressionStatement.of(ref),
                arguments
        );
    }

    public String getName() {
        return name;
    }

    public int getArity() {
        return arguments.size();
    }

    public List<CafeElement<?>> getArguments() {
        return arguments;
    }

    @Override
    protected FunctionInvocation self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunctionInvocation(this);
    }

    @Override
    public String toString() {
        return "FunctionInvocation{" +
                "name='" + name + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
