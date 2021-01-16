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

public class Block extends ExpressionStatement<Block> {
    private final List<CafeStatement<?>> statements = new LinkedList<>();
    private ReferenceTable referenceTable;
    private boolean hasReturn = false;

    private Block(ReferenceTable referenceTable) {
        this.referenceTable = referenceTable;
    }

    public static Block create(ReferenceTable referenceTable) {
        return new Block(referenceTable);
    }

    public ReferenceTable getReferenceTable() {
        return referenceTable;
    }

    public List<CafeStatement<?>> getStatements() {
        return statements;
    }

    @Override
    protected Block self() {
        return this;
    }

    public static Block empty() {
        return new Block(new ReferenceTable());
    }

    public Block add(Object statement) {
        if (statement != null)
            this.addStatement(CafeStatement.of(statement));
        return this;
    }

    private void addStatement(CafeStatement<?> statement) {
        statements.add(statement);
        updateStateWith(statement);
    }

    private void updateStateWith(CafeStatement<?> statement) {
        referenceTable.updateFrom(statement);
        checkForReturns(statement);
    }

    public static Block of(Object block) {
        if (block == null) {
            return empty();
        }
        if (block instanceof Block) {
            return (Block) block;
        }
        if (block instanceof CafeStatement<?>) {
            return empty().add(block);
        }
        throw cantConvert("Block", block);
    }


    public boolean hasReturn() {
        return hasReturn;
    }

    private void checkForReturns(CafeStatement<?> statement) {
        if (statement instanceof ReturnStatement)
            hasReturn = true;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitBlock(this);
    }
}
