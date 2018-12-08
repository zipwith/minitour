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

/** Abstract syntax for integer literals.
 */
public class IntLit extends Expr {

    /** The value of this integer literal.
     */
    private int num;

    /** Default constructor.
     */
    public IntLit(Position pos, int num) {
        super(pos);
        this.num = num;
    }

    /** Return a printable description of this expression.
     */
    public String toString() {
        return "" + num;
    }

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, "IntLit(" + num + ")");
    }

    /** Generate a pretty-printed description of this expression
     *  using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out) { out.print(Integer.toString(num)); }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        return node(dot, "IntLit(" + num + ")", n);
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
        /* Nothing to do in this case */
    }

    /** Run type checking analysis on this expression.  The typing parameter
     *  provides access to the scope analysis phase (in particular,
     *  to the associated error handler), and the env parameter
     *  reflects the environment in which the expression is evaluated.
     *  Unlike scope analysis for statements, there is no return
     *  result here: an expression cannot introduce new variables in
     *  to a program, so the final environment will always be the same
     *  as the initial environment.
     */
    public Type analyze(TypeAnalysis typing) {
        return type = Type.INT;
    }

    /** Run initialization analysis on this expression.  The init parameter
     *  provides access to the initialization analysis phase (in particular,
     *  to the associated error handler), and the initialized parameter
     *  reflects the set of variables that are known to have been initialized
     *  before this expression is evaluated.  The return result is the set of
     *  variables that are known to be initialized after the expression has
     *  been evaluated.  Because there are no side-effecting operations in
     *  the mini language described here, the return result is actually the
     *  same as the input value for initialized in all cases.  But, of
     *  course, this could change in future if new constructs were added to
     *  the language ...
     */
    public VarSet analyze(InitAnalysis init, VarSet initialized) {
        return initialized;
    }

    /** Rewrite this expression using algebraic identities to reduce
     *  the amount of computation that is required at runtime.  The
     *  algorithms used here implement a range of useful optimizations
     *  including, for example:
     *     x + 0  ==>  x
     *     n + m  ==>  (n+m)           if n,m are known integers
     *     (x + n) +m ==>  x + (n+m)   if n,m are known integers
     *  etc. with corresponding rules for *, &, |, and ^.  However,
     *  there are still plenty of other opportunities for simplification,
     *  including:
     *    use of identities/constant folding on Booleans
     *    removing double negations, complements, etc...
     *    distributivity properties, such as (x+n)+(y+m) ==> (x+y)+(n+m)
     *    and so on ...
     */
    Expr simplify() {
        return this;
    }

    /** Test to see if this expression is an integer literal.
     */
    IntLit isIntLit() { return this; }

    /** Return the numeric value for this integer literal.
     */
    public int getNum() {
        return num;
    }
}
