package cafelang.ir;

public class BreakContinueStatement extends CafeStatement<BreakContinueStatement>{
    public enum Type{
        BREAK, CONTINUE
    }

    private final Type type;
    private ForLoopStatement enclosingLoop;

    private BreakContinueStatement(Type type){
        this.type = type;
    }

    public static BreakContinueStatement newContinue(){
        return new BreakContinueStatement(Type.CONTINUE);
    }

    public static BreakContinueStatement newBreak(){
        return new BreakContinueStatement(Type.BREAK);
    }

    public BreakContinueStatement setEnclosingLoop(ForLoopStatement enclosingLoop) {
        this.enclosingLoop = enclosingLoop;
        return this;
    }

    public ForLoopStatement getEnclosingLoop() {
        return enclosingLoop;
    }

    public Type getType() {
        return type;
    }

    @Override
    protected BreakContinueStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitBreakContinue(this);
    }
}
