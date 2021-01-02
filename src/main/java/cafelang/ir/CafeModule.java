package cafelang.ir;

import java.util.*;

public class CafeModule extends CafeElement<CafeModule> {

    private final String moduleName;
    private final ReferenceTable globalReferenceTable;
    private CafeFunction initFunc;

    private Set<CafeFunction> functions = new LinkedHashSet<>();
    private List<CafeImport> imports = new LinkedList<>();

    public static final String INIT_FUNCTION = "#init";

    private static final CafeImport[] DEFAULT_IMPORTS = {
            CafeImport.of("*", "cafe.io.BasicIO"),
            CafeImport.of("*", "cafe.util.Conversions"),
            CafeImport.of("*", "cafe.DynamicObject"),
    };

    private CafeModule(String moduleName, ReferenceTable referenceTable) {
        this.moduleName = moduleName;
        this.globalReferenceTable = referenceTable;

        initFunc = CafeFunction.function(INIT_FUNCTION)
                               .block(
                                       Block.create(globalReferenceTable)
                               )
                               .asInit();
        functions.add(initFunc);
    }

    public static CafeModule create(String moduleName, ReferenceTable referenceTable) {
        return new CafeModule(moduleName, referenceTable);
    }

    public CafeModule add(CafeStatement<?> statement) {
        initFunc.getBlock()
                .add(statement);
        return this;
    }

    public void addFunction(CafeFunction function) {
        this.functions.add(function);
    }

    public void addImport(CafeImport cafeImport) {
        imports.add(cafeImport);
    }

    public Set<CafeImport> getImports() {
        Set<CafeImport> imp = new LinkedHashSet<>();
        imp.addAll(imports);
        Collections.addAll(imp, DEFAULT_IMPORTS);
        return imp;
    }

    @Override
    public List<CafeElement<?>> children() {
        LinkedList<CafeElement<?>> children = new LinkedList<>();
        children.addAll(getImports());
        children.addAll(functions);
        return children;
    }

    public CafeFunction getInitFunc() {
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
