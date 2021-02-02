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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class CafeModulePath extends ModulePath {
    private String className;
    private String stringPath;
    private Path path;

    public CafeModulePath(String path) {
        this.className = path.substring(path.lastIndexOf('/') + 1);
        this.stringPath = path;
        this.path = FileSystems.getDefault()
                               .getPath(path);
    }

    public Path getPath() {
        return path;
    }

    public String getClassName() {
        return className;
    }

    public String asString() {
        return stringPath;
    }

    @Override
    public void accept(ImportPathVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeModulePath urlPath = (CafeModulePath) o;
        try {
            return Files.isSameFile(path, urlPath.path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "CafeModulePath{" +
                "Path='" + stringPath + '\'' +
                '}';
    }
}
