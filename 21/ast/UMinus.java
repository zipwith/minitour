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

/** Abstract syntax for unary minus expressions.
 */
public class UMinus extends UnArithExpr {

    /** Default constructor.
     */
    public UMinus(Position pos, Expr exp) {
        super(pos, exp);
    }

    /** Return a string that provides a simple description of this
     *  particular type of operator node.
     */
    String label() { return "UMinus"; }

    /** Generate a pretty-printed description of this expression
     *  using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out) { unary(out, "-"); }

    /** Constant folding for unary operators with a known integer
     *  argument.
     */
    Expr fold(int n) { return new IntLit(pos, -n); }

    /** Evaluate this expression.
     */
    public int eval()
      throws Failure { return -exp.eval(); }

    /** Generate assembly language code for this expression that will
     *  evaluate the expression when it is executed and leave the result
     *  in the specified free register, preserving any lower numbered
     *  registers in the process.
     */
    public void compileExpr(IA32 a, int pushed, int free) {
        exp.compileExpr(a, pushed, free);
        a.emit("negl", a.reg(free));
    }
}
