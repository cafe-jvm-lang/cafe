package cafelang.ir;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CafeFunction extends ExpressionStatement<CafeFunction>{

    private String name;
    private Scope scope;
    private Block block;
    private List<String> parameterNames = new LinkedList<>();
    private boolean isSynthetic = false;
    private boolean isVarargs = false;
    private boolean isInit = false;

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

    public CafeFunction asInit(){
        isInit = true;
        return this;
    }

    public boolean isInit(){
        return isInit;
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

    public CafeFunction withVarargs(){
        isVarargs = true;
        return this;
    }

    public CafeFunction withParameters(Collection<?> names){
        for(Object name: names){
            addParamterToBlockReferences(name.toString());
            parameterNames.add(name.toString());
        }
        return this;
    }

    private void addParamterToBlockReferences(String name){
        this.getBlock().getReferenceTable().add(SymbolReference.of(name, SymbolReference.Kind.VAR));
    }

    public int getArity(){
        return parameterNames.size();
    }

    public List<String> getParameterNames(){
        return parameterNames;
    }

    @Override
    public List<CafeElement<?>> children() {
        return Collections.singletonList(block);
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
