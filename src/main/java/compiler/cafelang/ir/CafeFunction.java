package compiler.cafelang.ir;

import java.util.LinkedList;
import java.util.List;

public class CafeFunction extends ExpressionStatement<CafeFunction>{

    private String name;
    private Scope scope;
    private Block block;
    private List<String> parameterNames = new LinkedList<>();
    private boolean isSynthetic = false;
    private boolean isVarargs = false;

    public enum Scope{
        MODULE, CLOSURE
    }

    private CafeFunction(String name){
        this.name = name;
    }

    public static CafeFunction function(String name){
        return new CafeFunction(name);
    }

    public CafeFunction name(String n){
        name = n;
        return this;
    }

    public String getName(){
        return name;
    }

    public CafeFunction block(Block block){
        this.block = block;
        return this;
    }

    public Block getBlock() {
        return block;
    }

    public boolean isSynthetic(){
        return isSynthetic;
    }

    public boolean isVarargs(){
        return isVarargs;
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
