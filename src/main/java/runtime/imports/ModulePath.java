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

package runtime.imports;

import java.io.File;

public abstract class ModulePath {
    Class<?> module;

    public Class<?> getModule() {
        return module;
    }

    public void setModule(Class<?> module) {
        this.module = module;
    }

    public static ModulePath fromPath(String path) throws ClassNotFoundException {
        File f = new File(path + ".class");
        if (!f.exists()) {
            path = path.replaceAll("/", ".")
                       .trim();
            path = "library." + path;
            return new JavaModulePath(path, Class.forName(path));
        } else {
            return new URLModulePath(path);
        }
    }

    public abstract void accept(ImportPathVisitor v);

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
