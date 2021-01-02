package cafelang.ir;

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
