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

public class PropertyAccess extends ExpressionStatement<PropertyAccess> {
    private String name;

    private PropertyAccess(String name) {
        this.name = name;
    }

    public static PropertyAccess of(Object name) {
        if (name instanceof String)
            return new PropertyAccess((String) name);
        throw cantConvert("String", name);
    }

    public String getName() {
        return name;
    }

    @Override
    protected PropertyAccess self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitPropertyAccess(this);
    }

    @Override
    public String toString() {
        return "PropertyAccess{" +
                "name='" + name + '\'' +
                '}';
    }
}
