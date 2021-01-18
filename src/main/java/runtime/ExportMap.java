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

package runtime;

import library.DObject;

import java.util.Map;

public final class ExportMap {
    private Class<?> clazz;
    private DObject object;
    Map<String, Object> exports;

    private ExportMap(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static ExportMap of(Class<?> clazz) {
        return new ExportMap(clazz);
    }

    public ExportMap with(Map<String, Object> exports) {
        this.exports = exports;
        return this;
    }

    public Object getExport(String name) {
        return exports.get(name);
    }

    public DObject getAsDObject() {
        if (object != null)
            return object;
        object = DObjectCreator.create();
        for (Map.Entry<String, Object> entry : exports.entrySet()) {
            object.define(entry.getKey(), entry.getValue());
        }
        return object;
    }
}
