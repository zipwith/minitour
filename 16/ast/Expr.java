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
}
