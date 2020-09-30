package compiler.cafelang.ir;

public abstract class CafeStatement<T extends CafeStatement<T>> extends CafeElement<T>{

    public static CafeStatement<?>of(Object statement){
        // TODO: return null ?, not sure
        if(statement == null) return null;
        if(statement instanceof CafeStatement){
            return (CafeStatement) statement;
        }
        throw cantConvert("CafeStatement", statement);
    }

}
