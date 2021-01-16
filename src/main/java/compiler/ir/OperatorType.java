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

package compiler.ir;

import java.util.HashMap;

public enum OperatorType {

    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIVIDE("/"),
    MODULO("%"),

    POW("**"),
    FLOOR("//"),

    EQUALS("=="),
    NOTEQUALS("!="),
    LESS("<"),
    LESSOREQUALS("<="),
    MORE(">"),
    MOREOREQUALS(">="),

    BITOR("|"),
    BITAND("&"),
    BITXOR("^"),
    BITRIGHTSHIFT_SIGNED(">>"),
    BITRIGHTSHIFT_UNSIGNED(">>>"),
    BITLEFTSHIFT("<<"),

    AND("and"),
    OR("or"),
    NOT("not"),
    NOTOP("!"),

    IS("is"),
    ISNOT("isnot"),
    IN("in"),
    NOTIN("notin");

    private final String symbol;

    private static final HashMap<String, OperatorType> SYMBOL_MAPPING = new HashMap<>();

    static {
        for (OperatorType op : values()) {
            SYMBOL_MAPPING.put(op.toString(), op);
        }
    }

    OperatorType(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public static OperatorType of(Object value) {
        if (value instanceof OperatorType) {
            return (OperatorType) value;
        }
        if (SYMBOL_MAPPING.containsKey(value)) {
            return SYMBOL_MAPPING.get(value);
        }
        throw new IllegalArgumentException("An operator can't be create from " + value);
    }
}
