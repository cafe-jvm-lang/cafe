package cafelang.ir;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CafeModule extends CafeElement<CafeModule> {

    private String sourceFile;
    private final String clazz;
    private final ReferenceTable globalReferenceTable;
    private CafeFunction initFunc;

    private Set<CafeFunction> functions = new LinkedHashSet<>();

    public static final String INIT_FUNCTION = "#init";

    private CafeModule(String clazz, ReferenceTable referenceTable) {
        this.clazz = clazz;
        this.globalReferenceTable = referenceTable;

        initFunc = CafeFunction.function(INIT_FUNCTION)
                .block(
                        Block.create(globalReferenceTable)
                ).asInit();
        functions.add(initFunc);
    }

    public static CafeModule create(String clazz, ReferenceTable referenceTable){
        return new CafeModule(clazz,referenceTable);
    }

    public CafeModule add(CafeStatement<?> statement){
        initFunc.getBlock().add(statement);
        return this;
    }

    public void addFunction(CafeFunction function){
        this.functions.add(function);
    }

    @Override
    public List<CafeElement<?>> children(){
        LinkedList<CafeElement<?>> children  = new LinkedList<>();
        children.addAll(functions);
        return children;
    }

    public CafeFunction getInitFunc(){
        return initFunc;
    }

    @Override
    protected CafeModule self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitModule(this);
    }
}
