package cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class ConditionalBranching extends CafeStatement<ConditionalBranching> {

    private ExpressionStatement<?> condition;
    private Block trueBlock;
    private ConditionalBranching elseConditionalBranching;
    private Block falseBlock;

    private ConditionalBranching() {
    }

    public static ConditionalBranching branch() {
        return new ConditionalBranching();
    }

    public ConditionalBranching condition(Object cond) {
        if (cond == null) {
            this.condition = ConstantStatement.of(false);
        } else {
            this.condition = ExpressionStatement.of(cond);
        }
        return this;
    }

    public ConditionalBranching whenTrue(Object block) {
        this.trueBlock = Block.of(block);
        return this;
    }

    public ConditionalBranching otherwise(Object alternative) {
        if (alternative instanceof ConditionalBranching) {
            return elseBranch(alternative);
        }
        return whenFalse(alternative);
    }

    public ConditionalBranching elseBranch(Object elseBranch) {
        this.elseConditionalBranching = (ConditionalBranching) elseBranch;
        return this;
    }

    public ConditionalBranching whenFalse(Object block) {
        if (block == null) {
            this.falseBlock = null;
        } else {
            this.falseBlock = Block.of(block);
        }
        return this;
    }

    public ExpressionStatement<?> getCondition() {
        return condition;
    }

    public Block getTrueBlock() {
        return trueBlock;
    }

    public Block getFalseBlock() {
        return falseBlock;
    }

    public ConditionalBranching getElseConditionalBranching() {
        return elseConditionalBranching;
    }

    public boolean hasFalseBlock() {
        return falseBlock != null;
    }

    public boolean hasElseConditionalBranching() {
        return elseConditionalBranching != null;
    }

    @Override
    public List<CafeElement<?>> children() {
        LinkedList<CafeElement<?>> list = new LinkedList<>();
        list.add(condition);
        list.add(trueBlock);
        if (hasElseConditionalBranching())
            list.add(elseConditionalBranching);
        if (hasFalseBlock())
            list.add(falseBlock);
        return list;
    }

    @Override
    protected ConditionalBranching self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitConditionalBranching(this);
    }
}
