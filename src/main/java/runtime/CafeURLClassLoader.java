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

import runtime.imports.CafeModulePath;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class CafeURLClassLoader extends URLClassLoader {

    public CafeURLClassLoader(URL[] urls) {
        super(urls);
    }

    public static CafeURLClassLoader with(CafeModulePath module) {
        try {
            URL url = cafePathtoURL(module);
            URL[] urls = {url};
            return new CafeURLClassLoader(urls);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static URL cafePathtoURL(CafeModulePath path) throws MalformedURLException {
        String importPath = path.asString();
        String classpath = importPath + ".class";
        File f = new File(classpath);
        classpath = f.getParentFile()
                     .getPath();
        URL url = new File(classpath).toURI()
                                     .toURL();
        return url;
    }

    public void addModule(CafeModulePath path) {
        try {
            super.addURL(cafePathtoURL(path));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Class<?> loadModule(CafeModulePath module) throws ClassNotFoundException {
        return super.loadClass(module.getClassName());
    }
}
