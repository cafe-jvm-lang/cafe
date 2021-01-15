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

import java.util.HashMap;
import java.util.Map;

public class CafeImport extends CafeStatement<CafeImport> {
    private final Map<String, String> nameAlias;
    private final String modulePath;
    private boolean isDefault=false;

    private CafeImport(String modulePath) {
        nameAlias = new HashMap<>();
        this.modulePath = modulePath;
    }

    public static CafeImport of(String moduleName) {
        return new CafeImport(moduleName);
    }

    public void add(String name, String alias){
        nameAlias.put(name,alias);
    }

    public Map<String, String> getNameAlias() {
        return nameAlias;
    }

    public CafeImport asDefault(){
        isDefault = true;
        return this;
    }

    public boolean isDefault(){
        return isDefault;
    }

    public String getModulePath() {
        return modulePath;
    }

    @Override
    protected CafeImport self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitCafeImport(this);
    }

}
