/*
    Copyright 2018 Mark P Jones, Portland State University

    This file is part of minitour.

    minitour is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    minitour is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with minitour.  If not, see <https://www.gnu.org/licenses/>.
*/

package ast;
import compiler.Failure;
import compiler.Position;

/** Abstract syntax for logical and expressions (&&).
 */
public class LAnd extends BinLogicExpr {

    /** Default constructor.
     */
    public LAnd(Position pos, Expr left, Expr right) {
        super(pos, left, right);
    }

    /** Return a string that provides a simple description of this
     *  particular type of operator node.
     */
    String label() { return "LAnd"; }

    /** Generate a pretty-printed description of this expression
     *  using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out) { binary(out, "&&"); }

    /** Evaluate this expression.
     */
    public int eval()
      throws Failure { return fromBool(toBool(left.eval()) && toBool(right.eval())); }

    /** Generate assembly language code for this expression that will
     *  evaluate the expression when it is executed and leave the result
     *  in the specified free register, preserving any lower numbered
     *  registers in the process.
     */
    public void compileExpr(IA32 a, int pushed, int free) {
        String lab = a.newLabel();
        left.compileExpr(a, pushed, free);
        a.emit("orl", a.reg(free), a.reg(free));
        a.emit("jz",  lab);
        right.compileExpr(a, pushed, free);
        a.emitLabel(lab);
      }

    /** Generate code that will evaluate this (boolean-valued) expression
     *  and jump to the specified label if the result is true.
     */
    void branchTrue(IA32 a, int pushed, int free, String lab) {
        String lab1 = a.newLabel();
        left.branchFalse(a, pushed, free, lab1);
        right.branchTrue(a, pushed, free, lab);
        a.emitLabel(lab1);
    }

    /** Generate code that will evaluate this (boolean-valued) expression
     *  and jump to the specified label if the result is false.
     */
    void branchFalse(IA32 a, int pushed, int free, String lab) {
        left.branchFalse(a, pushed, free, lab);
        right.branchFalse(a, pushed, free, lab);
    }
}
