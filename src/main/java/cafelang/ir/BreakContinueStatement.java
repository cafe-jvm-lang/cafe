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

package cafelang.ir;

public class BreakContinueStatement extends CafeStatement<BreakContinueStatement>{
    public enum Type{
        BREAK, CONTINUE
    }

    private final Type type;
    private ForLoopStatement enclosingLoop;

    private BreakContinueStatement(Type type){
        this.type = type;
    }

    public static BreakContinueStatement newContinue(){
        return new BreakContinueStatement(Type.CONTINUE);
    }

    public static BreakContinueStatement newBreak(){
        return new BreakContinueStatement(Type.BREAK);
    }

    public BreakContinueStatement setEnclosingLoop(ForLoopStatement enclosingLoop) {
        this.enclosingLoop = enclosingLoop;
        return this;
    }

    public ForLoopStatement getEnclosingLoop() {
        return enclosingLoop;
    }

    public Type getType() {
        return type;
    }

    @Override
    protected BreakContinueStatement self() {
        return this;
    }

    @Override
    public void accept(CafeIrVisitor visitor) {
        visitor.visitBreakContinue(this);
    }
}
