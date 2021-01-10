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

package compiler.util;

public class Position {
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    private Position(int startColumn, int startLine, int endColumn, int endLine) {
        this.startColumn = startColumn;
        this.startLine = startLine;
        this.endColumn = endColumn;
        this.endLine = endLine;
    }

    public static Position of(int column, int line) {
        return new Position(column, line, column, line);
    }

    public static Position of(int startColumn, int startLine, int endColumn, int endLine) {
        return new Position(startColumn, startLine, endColumn, endLine);
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public int getEndLine() {
        return endLine;
    }
}
