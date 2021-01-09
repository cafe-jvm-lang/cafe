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

package compiler.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CharReader {

    /**
     * Input Buffer
     */
    protected List<Character> buff;

    protected int lineNumber = 1;
    protected int column = 0;

    /**
     * Input Buffer pointer
     */
    protected int bp = -1;

    /**
     * Input Buffer Length
     */
    private int buffLen;

    /**
     * Saved buffer
     */
    protected List<Character> sbuff;

    /**
     * Saved Buffer pointer
     */
    private int sp = -1;

    /**
     * Saved Buffer max capacity
     */
    private int sbuffCapacity = 128;

    /**
     * Current character
     */
    protected char ch;
//
//	protected CharReader(CharSequence input) {
//		this(input.toString().toCharArray());
//	}

    protected CharReader(List<Character> input) {
        buff = input;
        buffLen = buff.size();
        scanChar();

        sbuff = new ArrayList<>(sbuffCapacity);
    }

    protected void scanChar() {
        if (bp < buffLen - 1) {
            ch = buff.get(++bp);
            if (ch == '\n') {
                lineNumber++;
                column = 0;
            } else if (ch == '\t')
                column += 4;
            else
                column++;
        } else
            ch = Character.MIN_VALUE;
    }

    protected void putChar(char c) {
        sbuff.add(c);
    }

    /**
     * @param clearSavedBuffer - if true, clears sbuff
     * @return saved buffer char array
     */
    protected List<Character> getSavedBuffer(boolean clearSavedBuffer) {
        if (clearSavedBuffer) {
            clearSavedBufer();
        }
        return sbuff;
    }

    protected String getSavedBufferAsString(boolean clearSavedBuffer) {
        String s = sbuff.stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());
        if (clearSavedBuffer) {
            clearSavedBufer();
        }
        return s;
    }

    private void clearSavedBufer() {
        sbuff = new ArrayList<>(sbuffCapacity);
    }

}
