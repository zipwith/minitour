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
import compiler.Position;

/** Abstract syntax for if-then-else statements.
 */
public class If extends PosStmt {

    /** The test expression.
     */
    private Expr test;

    /** The true branch.
     */
    private Stmt ifTrue;

    /** The false branch.
     */
    private Stmt ifFalse;

    /** Default constructor.
     */
    public If(Position pos, Expr test, Stmt ifTrue, Stmt ifFalse) {
        super(pos);
        this.test = test;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, "If");
        test.indent(out, n+1);
        ifTrue.indent(out, n+1);
        ifFalse.indent(out, n+1);
    }

    /** Generate a pretty-printed description of this abstract syntax
     *  node using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out, int n) {
        out.indent(n);
        out.print("if (");
        test.print(out);
        out.print(")");
        ifTrue.printThenElse(out, n, ifFalse);
    }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        return ifFalse.toDot(dot, n, "ifFalse",
               ifTrue.toDot(dot, n, "ifTrue",
               test.toDot(dot, n, "test",
               node(dot, "If", n))));
    }
}
