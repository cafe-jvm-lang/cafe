package compiler.cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class CafeFunction extends ExpressionStatement<CafeFunction>{

    private String name;
    private Scope scope;
    private Block block;
    private List<String> parameterNames = new LinkedList<>();

    public enum Scope{
        MODULE, CLOSURE
    }


    @Override
    protected CafeFunction self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitFunction(this);
    }
}
