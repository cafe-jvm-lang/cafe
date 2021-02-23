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
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Cafe Function. Every function is an instance of this class. Holds a reference to a JVM method using a method-handle.
 */
public class DFunc extends DObject {
    private MethodHandle handle;
    private boolean isVarity = false;

    public DFunc(DObject __proto__, MethodHandle handle) {
        super(__proto__);
        this.handle = handle;
        if (handle.isVarargsCollector())
            isVarity = true;
    }

    public Object invoke(Object... args) throws Throwable {
        List<Object> arguments = new ArrayList<>();
        int pos = 0;
        if (handle.type()
                  .parameterArray()[0] != DObject.class) {
            // if 1st arg is already bound, omit the thisP arg from args.
            pos = 1;
        }
        for (int i = pos; i < args.length; i++)
            arguments.add(args[i]);

        return handle.invokeWithArguments(arguments);
    }

    public MethodHandle handle() {
        return handle;
    }

    public boolean isVarity() {
        return isVarity;
    }

    @Override
    public String toString() {
        return "Function{  }";
    }
}
