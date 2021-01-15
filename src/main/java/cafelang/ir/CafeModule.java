/*
 * Copyright (c) 2021. Dhyey Shah, Saurabh Pethani, Romil Nisar
 *
 * Developed by:
 *         Dhyey Shah<dhyeyshah4@gmail.com>
 *         https://github.com/dhyey-shah
 *
 * Contributors:
 *         Saurabh Pethani<spethani28@gmail.com>
 *         https://github.com/SaurabhPethani
 *
 *         Romil Nisar<rnisar7@gmail.com>
 *
 *
 * This file is part of Cafe.
 *
 * Cafe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3 of the License.
 *
 * Cafe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cafe.  If not, see <https://www.gnu.org/licenses/>.
 */

package cafelang.ir;

import java.util.*;

public class CafeModule extends CafeElement<CafeModule> {

    private final String moduleName;
    private final ReferenceTable globalReferenceTable;
    private CafeFunction initFunc;

    private Set<CafeFunction> functions = new LinkedHashSet<>();
    private List<CafeImport> imports = new LinkedList<>();
    private List<CafeExport> exports = new LinkedList<>();

    public static final String INIT_FUNCTION = "#init";

    private static final CafeImport[] DEFAULT_IMPORTS = {
            CafeImport.of("cafe.io.BasicIO").asDefault(),
            CafeImport.of("cafe.util.Conversions").asDefault(),
            CafeImport.of("cafe.DynamicObject").asDefault(),
    };

    private CafeModule(String moduleName, ReferenceTable referenceTable) {
        this.moduleName = moduleName;
        this.globalReferenceTable = referenceTable;

        initFunc = CafeFunction.function(INIT_FUNCTION)
                               .block(
                                       Block.create(globalReferenceTable)
                               )
                               .asInit();
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

    public void addExport(CafeExport cafeExport){
        exports.add(cafeExport);
    }

    public Set<CafeImport> getImports() {
        Set<CafeImport> imp = new LinkedHashSet<>();
        imp.addAll(imports);
        //Collections.addAll(imp, DEFAULT_IMPORTS);
        return imp;
    }

    public Set<CafeExport> getExports() {
        return new HashSet<>(exports);
    }

    @Override
    public List<CafeElement<?>> children() {
        LinkedList<CafeElement<?>> children = new LinkedList<>();
        children.addAll(getImports());
        children.addAll(functions);
        children.add(initFunc);
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
