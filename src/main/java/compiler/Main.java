/*
 * Copyright (c) 2021. Dhyey Shah <dhyeyshah4@gmail.com>
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

package compiler;

public class Main {

    /**
     * Entry point into the compiler
     *
     * @param args expects a file-name.txt file
     */
    public static void main(String[] args) {
        System.exit(compile(args));
    }

    public static int compile(String[] args) {
        compiler.main.Main compiler = new compiler.main.Main();
        return compiler.compile(args).exitCode;
    }
}
