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

import java.util.Collections;
import java.util.List;

public class DeclarativeAssignmentStatement extends AssignedStatement {

    private SymbolReference symbolReference;
    private ExpressionStatement<?> expressionStatement;
    private boolean isAssigned = true;

    private DeclarativeAssignmentStatement() {
    }

    public static DeclarativeAssignmentStatement create(SymbolReference ref, Object expr) {
        return new DeclarativeAssignmentStatement().to(ref)
                                                   .as(expr);
    }

    public DeclarativeAssignmentStatement to(SymbolReference ref) {
        symbolReference = ref;
        return this;
    }

    public DeclarativeAssignmentStatement as(Object value) {
        if (value == null) {
            expressionStatement = new NullStatement();
            isAssigned = false;
        } else
            expressionStatement = ExpressionStatement.of(value);
        return this;
    }

    public SymbolReference getSymbolReference() {
        return symbolReference;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Collections.singletonList(expressionStatement);
    }

    @Override
    protected DeclarativeAssignmentStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitDeclarativeAssignment(this);
    }
}
