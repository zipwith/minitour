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

/** Abstract syntax for identifiers/variables.
 */
public class Id extends Expr {

    /** The identifier name.
     */
    String name;

    /** Default constructor.
     */
    public Id(Position pos, String name) {
        super(pos);
        this.name = name;
    }

    /** Return a printable description of this expression.
     */
    public String toString() {
        return name;
    }

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, "Id(\"" + name + "\")");
    }

    /** Generate a pretty-printed description of this expression
     *  using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out) { out.printUse(this); }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        return node(dot, "Id(\\\"" + name + "\\\")", n);
    }

    /** Holds a pointer to the environment entry for this identifier.
     *  This field will be initialized to a non-null value during the
     *  scope analysis phase.
     */
    private Env v = null;

    /** Extend the given environment with an entry for this variable,
     *  adding a link from this identifier to the new environment slot.
     */
    public Env extend(Type type, Env env) {
        return v = new Env(this, type, env);
    }

    /** Return the name associated with this identifier.
     */
    public String getName() {
        return name;
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
        v = Env.lookup(this, env);
        if (v==null) {
          scoping.report(new Failure(pos, "Identifier \"" + name
                                           + "\" has not been declared"));
        }
    }

    /** Generate a dot description for the environment structure of this
     *  program.
     */
    public void dotEnv(DotEnvOutput dot) {
        v.dotEnv(dot);
    }

    /** Print out a description of this identifier as plain text.
     */
    void printText(TextOutput out) { out.print(name); }

    /** Print out an HTML description corresponding to the definition of
     *  this identifier.  This amounts to wrapping the name in an HTML
     *  span so that it can be referenced by JavaScript code at each
     *  point of use.
     */
    void printDefHTML(HTMLOutput html) {
        String       me    = pos.coordString();
        StringBuffer mover = new StringBuffer("defId"+me);
        StringBuffer mout  = new StringBuffer("normalId"+me);
        for (IdList uses = (v!=null) ? v.getUses() : null; uses!=null; uses=uses.rest) {
            String ps = uses.head.pos.coordString();
            mover.append(";highlightId");
            mover.append(ps);
            mout.append(";normalId");
            mout.append(ps);
        }
        printHTML(html, me, mover.toString(), mout.toString());
    }

    /** Print out an HTML description corresponding to a use of
     *  this identifier.  This amounts to wrapping the name in an HTML
     *  span with associated JavaScript code for mouse over and mouse
     *  out events that change the highlighting of the current node
     *  as well as the defining occurrence.
     */
    void printUseHTML(HTMLOutput html) {
        String me = pos.coordString();
        if (v==null) {
            printHTML(html, me, "useId"+me, "normalId"+me);
        } else {
            String mydef = v.getId().pos.coordString();
            printHTML(html, me, "useId"+me+";highlightId"+mydef,
                                "normalId"+me+";normalId"+mydef);
        }
    }

    /** Print a string that is wrapped in a span with the HTML identifier me,
     *  and mouse over and mouseout actions as specified by the given strings.
     */
    void printHTML(HTMLOutput html, String me, String mouseover, String mouseout) {
        html.print("<span class=\"normal\" id=\"");
        html.print(me);
        html.print("\" title=\"");
        html.print(me);
        html.print("\" onmouseover=\"");
        html.print(mouseover);
        html.print("\" onmouseout=\"");
        html.print(mouseout);
        html.print("\">");
        html.print(name);
        html.print("</span>");
    }

    /** Set the type of this Id.
     */
    void setType(Type type) { this.type = type; }

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
        return type = v.getType();
    }

    /** Add an entry for the variable (i.e., environment entry) that is
     *  associated with this identifier to the given set of variables if
     *  it is not already included.
     */
    VarSet addTo(VarSet vars) {
        return VarSet.includes(v, vars) ? vars : new VarSet(v, vars);
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
        if (!VarSet.includes(v, initialized)) {
            init.report(new Failure(pos,
                            "The variable \"" + this
                            + "\" may be used before it has been initialized"));
        }
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

    /** Evaluate this expression.
     */
    public int eval()
      throws Failure { return this.load(); }

    /** Return the environment entry that is associated with this identifier.
     */
    Env getEnv()
      throws Failure {
        if (v==null) {
            throw new Failure(pos, "Unbound variable \"" + name + "\"");
        }
        return v;
    }

    /** Return the current value of a variable.
     */
    int load()
      throws Failure { return getEnv().load(); }

    /** Set a new value for a variable.
     */
    void store(int k)
      throws Failure { getEnv().store(k); }
}
