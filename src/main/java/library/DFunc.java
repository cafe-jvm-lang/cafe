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

package library;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class DFunc extends DObject {
    private MethodHandle handle;
    private DObject thisP = null;
    private List<Object> arguments = new LinkedList<>();

    public DFunc(MethodHandle handle) {
        this.handle = handle;
    }

    public Object invoke(Object... args) throws Throwable {
        return handle.invokeWithArguments(args);
    }

    public MethodHandle handle() {
        return handle;
    }

    public DFunc bind(DObject thisP, Object... args) {
        if (thisP != null) {
            this.thisP = thisP;
            handle = handle.bindTo(thisP);
        }
        if (args.length > 0) {
            handle = MethodHandles.insertArguments(handle, 1, args);
        }
        return this;
    }

    @Override
    public String toString() {
        return "Function{" + super.map + "}";
    }
}
