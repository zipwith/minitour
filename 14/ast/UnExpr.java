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

/** Abstract syntax for unary expressions.
 */
public abstract class UnExpr extends Expr {

    /** The operand of a unary operator expression.
     */
    protected Expr exp;

    /** Default constructor.
     */
    public UnExpr(Position pos, Expr exp) {
        super(pos);
        this.exp = exp;
    }

    /** Return a string that provides a simple description of this
     *  particular type of operator node.
     */
    abstract String label();

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, label());
        exp.indent(out, n+1);
    }

    /** Print out this unary expression.
     */
    protected void unary(TextOutput out, String op) {
        out.print(op);
        exp.parenPrint(out);
    }

    /** Print out this expression, wrapping it in parentheses if the
     *  expression includes a binary or unary operand.
     */
    public void parenPrint(TextOutput out) {
        out.print("(");
        this.print(out);
        out.print(")");
    }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        return exp.toDot(dot, n, "exp",
               node(dot, label(), n));
    }

    /** Run scope analysis on this expression.  The scoping parameter
     *  provides access to the scope analysis phase (in particular,
     *  to the associated error handler), and the env parameter
     *  reflects the environment in which the expression is evaluated.
     *  Unlike scope analysis for statements, there is no return
     *  result here: an expression cannot introduce new variables in
     *  to a program, so the final environment will always be the same
     *  as the initial environment.
     */
    public void analyze(ScopeAnalysis scoping, Env env) {
        exp.analyze(scoping, env);
    }
}
