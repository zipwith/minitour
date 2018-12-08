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
import compiler.Failure;

/** Abstract syntax for binary expressions.
 */
public abstract class BinExpr extends Expr {

    /** The left subexpression.
     */
    protected Expr left;

    /** The right subexpression.
     */
    protected Expr right;

    /** Default constructor.
     */
    public BinExpr(Position pos, Expr left, Expr right) {
        super(pos);
        this.left = left;
        this.right = right;
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
        left.indent(out, n+1);
        right.indent(out, n+1);
    }

    /** Print out this binary expression.
     */
    protected void binary(TextOutput out, String op) {
        left.parenPrint(out);
        out.print(op);
        right.parenPrint(out);
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
        return right.toDot(dot, n, "right",
               left.toDot(dot, n, "left",
               node(dot, label(), n)));
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
        left.analyze(scoping, env);
        right.analyze(scoping, env);
    }

    /** Check that two type arguments, corresponding to the types of a
     *  binary operator's arguments, are equal and report an error if
     *  they differ.
     */
    void requireSame(TypeAnalysis typing, Type lt, Type rt) {
        if (lt!=rt) {
          typing.report(new Failure(pos, "Types of left operand (" + lt +
                                         ") and right operand (" + rt +
                                         ") do not match"));
        }
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
        return right.analyze(init, left.analyze(init, initialized));
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
        left            = left.simplify();
        right           = right.simplify();
        IntLit leftInt  = left.isIntLit();
        IntLit rightInt = right.isIntLit();
        if (leftInt==null) {
            return (rightInt==null) ? this : this.simpR(rightInt.getNum());
        } else {
            return (rightInt==null) ? this.simpL(leftInt.getNum())
                                    : this.fold(leftInt.getNum(),
                                                rightInt.getNum());
        }
    }

    /** Constant folding for binary operators with two known integer
     *  arguments.
     */
    Expr fold(int n, int m) { return this; }

    /** Simplification of a binary expression when the left operand
     *  (but not the right) is a known integer constant.
     */
    Expr simpL(int m) {
        // Default behavior is to leave expression unchanged.
        return this;
    }

    /** Simplification of a binary expression when the right operand
     *  (but not the left) is a known integer constant.
     */
    Expr simpR(int m) { return this; }
}
