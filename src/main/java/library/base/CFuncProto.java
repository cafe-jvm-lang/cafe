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

package library.base;

import library.DFunc;
import library.DObject;
import runtime.DObjectCreator;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class CFuncProto {
    public static DFunc bind(DObject func, DObject thisP, Object... args) {
        MethodHandle h1 = ((DFunc) func).handle();
        MethodHandle h2 = null;
        boolean isVarity = ((DFunc) func).isVarity();

        if (thisP == null && args.length > 0)
            // this is required as arguments array is not spread when thisP is null.
            // args = { args[] }
            args = (Object[]) args[0];

        if (thisP != null) {
            h2 = h1.bindTo(thisP);
        }

        if (args.length > 0) {
            int pos = 1;
            MethodHandle mh = h1;
            if (thisP != null) {
                pos = 0;
                mh = h2;
            }
            h2 = MethodHandles.insertArguments(mh, pos, args);
        }

        if (h2 != null) {
            if (isVarity)
                h2 = h2.asVarargsCollector(Object[].class);
            return DObjectCreator.createFunc(h2);
        }

        return (DFunc) func;
    }
}
