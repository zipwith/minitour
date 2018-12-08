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

/** Abstract syntax for expressions.
 */
public abstract class Expr {

    protected Position pos;

    /** Default constructor.
     */
    public Expr(Position pos) {
        this.pos = pos;
    }

    /** Return a string describing the position/coordinates
     *  of this abstract syntax tree node.
     */
    String coordString() { return pos.coordString(); }

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public abstract void indent(IndentOutput out, int n);

    /** Generate a pretty-printed description of this expression
     *  using the concrete syntax of the mini programming language.
     */
    public abstract void print(TextOutput out);

    /** Print out this expression, wrapping it in parentheses if the
     *  expression includes a binary or unary operand.
     */
    public void parenPrint(TextOutput out) {
        this.print(out);
    }

    /** Output a description of this node (with id n), including a
     *  link to its parent node (with id p) and returning the next
     *  available node id.
     */
    public int toDot(DotOutput dot, int p, String attr, int n) {
        dot.join(p, n, attr);
        return toDot(dot, n);
    }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public abstract int toDot(DotOutput dot, int n);

    /** Output a dot description of this abstract syntax node
     *  using the specified label and id number.
     */
    protected int node(DotOutput dot, String lab, int n) {
        return dot.node(lab + "\\n" + pos.coordString(), Type.color(type), n);
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
    public abstract void analyze(ScopeAnalysis scoping, Env env);

    protected Type type = null;

    /** Run type checking analysis on this expression.  The typing parameter
     *  provides access to the scope analysis phase (in particular,
     *  to the associated error handler), and the env parameter
     *  reflects the environment in which the expression is evaluated.
     *  Unlike scope analysis for statements, there is no return
     *  result here: an expression cannot introduce new variables in
     *  to a program, so the final environment will always be the same
     *  as the initial environment.
     */
    public abstract Type analyze(TypeAnalysis typing);

    /** Run the type checking analysis on an expression that is required to
     *  have the type specified by the expected parameter.
     */
    Type require(TypeAnalysis typing, Type expected) {
        Type t = analyze(typing);
        if (t!=expected) {
          typing.report(new Failure(pos, "An expression of type " + expected +
                                         " was expected"));
          return expected;
        }
        return t;
      }

    /** Run the type checking analysis on an expression that is required to
     *  have either the type specified by the expected parameter or else the
     *  type specified by the alternative parameter.
     */
    Type require(TypeAnalysis typing, Type expected, Type alternative) {
        Type t = analyze(typing);
        if (t!=expected && t!=alternative) {
          typing.report(new Failure(pos, "An expression of type " + expected +
                                         " was expected"));
          return expected;
        }
        return t;
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
    public abstract VarSet analyze(InitAnalysis init, VarSet initialized);

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
    abstract Expr simplify();

    /** Simplify an addition with a known integer as the right argument.
     */
    Expr simpAdd(Add orig, int m) { return newAdd(orig.pos, m); }

    /** Construct an abstract syntax tree for an addition with a known
     *  integer as the right argument.
     */
    Expr newAdd(Position pos, int n) {
        return (n==0) ? this : new Add(pos, this, new IntLit(pos, n));
    }

    /** Simplify a multiplication with a known integer as the right argument.
     */
    Expr simpMul(Mul orig, int m) { return newMul(orig.pos, m); }

    /** Construct an abstract syntax tree for a multiplication with a known
     *  integer as the right argument.
     */
    Expr newMul(Position pos, int n) {
        return (n==1) ? this                 // x * 1 == x
             : (n==0) ? new IntLit(pos, 0)   // x * 0 == 0
             : new Mul(pos, this, new IntLit(pos, n));
    }

    /** Simplify a bitwise and with a known integer as the right argument.
     */
    Expr simpBAnd(BAnd orig, int m) { return newBAnd(orig.pos, m); }

    /** Construct an abstract syntax tree for a bitwise and with a known
     *  integer as the right argument.
     */
    Expr newBAnd(Position pos, int n) {
        return (n==(-1)) ? this                // x & (-1) == x
             : (n==0)    ? new IntLit(pos, 0)  // x & 0    == 0
             : new BAnd(pos, this, new IntLit(pos, n));
    }

    /** Simplify a bitwise or with a known integer as the right argument.
     */
    Expr simpBOr(BOr orig, int m) { return newBOr(orig.pos, m); }

    /** Construct an abstract syntax tree for a bitwise or with a known
     *  integer as the right argument.
     */
    Expr newBOr(Position pos, int n) {
        return (n==(-1)) ? new IntLit(pos, -1) // x | (-1) == (-1)
             : (n==0)    ? this                // x | 0    == x
             : new BOr(pos, this, new IntLit(pos, n));
    }

    /** Simplify a bitwise xor with a known integer as the right argument.
     */
    Expr simpBXor(BXor orig, int m) { return newBXor(orig.pos, m); }

    /** Construct an abstract syntax tree for a bitwise xor with a known
     *  integer as the right argument.
     */
    Expr newBXor(Position pos, int n) {
        return (n==(-1)) ? new BNot(pos, this) // x ^ (-1) == ~x
             : (n==0)    ? this                // x ^ 0    == x
             : new BXor(pos, this, new IntLit(pos, n));
    }

    /** Test to see if this expression is an integer literal.
     */
    IntLit isIntLit() { return null; }

    /** Evaluate this expression.
     */
    public abstract int eval()
      throws Failure;

    /** Return the integer corresponding to this boolean, assuming that
     *  false and true are represented by the integers 0 and 1, resp.
     */
    protected int fromBool(boolean b) {
        return b ? 1 : 0;
    }

    /** Return the boolean corresponding to this integer, assuming that
     *  false and true are represented by the integers 0 and 1, resp.
     *  (In theory, 0 and 1 are the only possible inputs, but we will
     *  also treat any other input as representing true.)
     */
    protected boolean toBool(int n) {
        return n!=0;
    }
}
