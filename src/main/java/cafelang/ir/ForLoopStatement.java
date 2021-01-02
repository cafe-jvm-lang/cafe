package cafelang.ir;

import java.util.List;

public class ForLoopStatement extends CafeStatement<ForLoopStatement> {
    private List<AssignedStatement> initStatement = null;
    private ExpressionStatement<?> condition;
    private List<CafeStatement<?>> postStatement = null;
    private Block block = null;

    private ForLoopStatement() {
    }

    public static ForLoopStatement loop() {
        return new ForLoopStatement();
    }

    public ForLoopStatement init(List<AssignedStatement> list) {
        initStatement = list;
        return this;
    }

    public ForLoopStatement condition(ExpressionStatement<?> expression) {
        condition = expression;
        return this;
    }

    public ForLoopStatement block(Block block) {
        this.block = block;
        return this;
    }

    public ForLoopStatement postStatement(List<CafeStatement<?>> post) {
        postStatement = post;
        return this;
    }

    public List<AssignedStatement> getInitStatements() {
        return initStatement;
    }

    public Block getBlock() {
        return block;
    }

    public List<CafeStatement<?>> getPostStatements() {
        return postStatement;
    }

    public ExpressionStatement<?> getCondition() {
        return condition;
    }

    public boolean hasInitStatement() {
        if (initStatement != null)
            return true;
        return false;
    }

    public boolean hasPostStatement() {
        if (postStatement != null)
            return true;
        return false;
    }

    @Override
    protected ForLoopStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitForLoop(this);
    }
}
