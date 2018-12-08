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

/** Abstract syntax for while statements.
 */
public class While extends PosStmt {

    /** The test expression.
     */
    private Expr test;

    /** The body of this loop.
     */
    private Stmt body;

    /** Default constructor.
     */
    public While(Position pos, Expr test, Stmt body) {
        super(pos);
        this.test = test;
        this.body = body;
    }

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, "While");
        test.indent(out, n+1);
        body.indent(out, n+1);
    }

    /** Generate a pretty-printed description of this abstract syntax
     *  node using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out, int n) {
        out.indent(n);
        out.print("while (");
        test.print(out);
        out.print(")");
        body.printElse(out, n);
    }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        return body.toDot(dot, n, "body",
               test.toDot(dot, n, "test",
               node(dot, "While", n)));
    }

    /** Run scope analysis on this statement.  The scoping parameter
     *  provides access to the scope analysis phase (in particular,
     *  to the associated error handler), and the env parameter
     *  reflects the environment at the start of the statement.  The
     *  return result is the environment at the end of the statement.
     */
    public Env analyze(ScopeAnalysis scoping, Env env) {
        test.analyze(scoping, env);
        body.analyze(scoping, env);
        return env;
    }

    /** Generate a dot description for the environment structure of this
     *  program.
     */
    public void dotEnv(DotEnvOutput dot) {
        body.dotEnv(dot);
    }

    /** Run type checker on this statement.  The typing parameter
     *  provides access to the scope analysis phase (specifically,
     *  to the associated error handler).
     */
    public void analyze(TypeAnalysis typing) {
        test.require(typing, Type.BOOLEAN);
        body.analyze(typing);
    }

    /** Run initialization analysis on this statement.  The init
     *  parameter provides access to an initialization analysis phase
     *  object (specifically, to an associated error handler).  The
     *  initialized parameter is the set of variables (each represented
     *  by pointers to environment entries) that have definitely been
     *  initialized before this statement is executed.
     */
    public VarSet analyze(InitAnalysis init, VarSet initialized) {
        initialized = test.analyze(init, initialized);
        body.analyze(init, initialized); // Note: result is discarded
        return initialized;
    }

    /** Attempt to simplify all of the expressions in this statement.
     */
    public void simplify() {
        test = test.simplify();
        body.simplify();
    }

    /** Execute this program.
     */
    public void exec()
      throws Failure {
        while (test.eval()!=0) {
            body.exec();
        }
    }
}
