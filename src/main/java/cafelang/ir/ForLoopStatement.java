package cafelang.ir;

import java.util.List;

public class ForLoopStatement extends CafeStatement<ForLoopStatement>{
    private List<DeclarativeAssignmentStatement> initStatement=null;
    private ExpressionStatement<?> condition;
    private List<CafeStatement<?>> postStatement = null;

    private ForLoopStatement(){}

    public static ForLoopStatement loop(){
        return new ForLoopStatement();
    }

    public ForLoopStatement init(List<DeclarativeAssignmentStatement> list){
        initStatement = list;
        return this;
    }

    public ForLoopStatement condition(ExpressionStatement<?> expression){
        condition = expression;
        return this;
    }

    public ForLoopStatement postStatement(List<CafeStatement<?>> post){
        postStatement = post;
        return this;
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
