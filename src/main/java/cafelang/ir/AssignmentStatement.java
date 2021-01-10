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

import java.util.LinkedList;
import java.util.List;

public class AssignmentStatement extends AssignedStatement {

    private ExpressionStatement<?> lhsExpression;
    private ExpressionStatement<?> rhsExpression;

    public static AssignmentStatement create(Object ref, Object value) {
        return new AssignmentStatement().to(ref)
                                        .as(value);
    }

    public AssignmentStatement to(Object ref) {
        if (ref == null) {
            throw new IllegalArgumentException("Must assign to a reference");
        }
        this.lhsExpression = ExpressionStatement.of(ref);
        return this;
    }

    public AssignmentStatement as(Object expr) {

        this.rhsExpression = ExpressionStatement.of(expr);

        return this;
    }

    public ExpressionStatement<?> getLhsExpression() {
        return lhsExpression;
    }

    public ExpressionStatement<?> getRhsExpression() {
        return rhsExpression;
    }

    @Override
    public List<CafeElement<?>> children() {
        LinkedList<CafeElement<?>> children = new LinkedList<>();
        //children.add(lhsExpression);
        children.add(rhsExpression);
        return children;
    }

    @Override
    protected AssignmentStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitAssignment(this);
    }

//    @Override
//    public List<SymbolReference> getReferences() {
//        return List.of(symbolReference);
//    }
}
