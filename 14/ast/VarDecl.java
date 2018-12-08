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

/** Abstract syntax for variable declarations.
 */
public class VarDecl extends PosStmt {

    /** The type of the declared variables.
     */
    private Type type;

    /** The names of the declared variables.
     */
    private Id[] vars;

    /** Default constructor.
     */
    public VarDecl(Position pos, Type type, Id[] vars) {
        super(pos);
        this.type = type;
        this.vars = vars;
    }

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, "VarDecl");
        out.indent(n+1, type.toString());
        for (int i=0; i<vars.length; i++) {
           vars[i].indent(out, n+1);
        }
    }

    /** Generate a pretty-printed description of this abstract syntax
     *  node using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out, int n) {
        out.indent(n);
        out.print(type.toString());
        out.print(" ");
        for (int i=0; i<vars.length; i++) {
           if (i>0) {
               out.print(", ");
           }
           out.printDef(vars[i]);
        }
        out.println(";");
    }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        int c = type.toDot(dot, n, "type", node(dot, "VarDecl", n));
        for (int i=0; i<vars.length; i++) {
            c = vars[i].toDot(dot, n, "" + i, c);
        }
        return c;
    }

    /** Run scope analysis on this statement.  The scoping parameter
     *  provides access to the scope analysis phase (in particular,
     *  to the associated error handler), and the env parameter
     *  reflects the environment at the start of the statement.  The
     *  return result is the environment at the end of the statement.
     */
    public Env analyze(ScopeAnalysis scoping, Env env) {
        for (int i=0; i<vars.length; i++) {
            env = vars[i].extend(type, env);
        }
        return env;
    }

    /** Generate a dot description for the environment structure of this
     *  program.
     */
    public void dotEnv(DotEnvOutput dot) {
        for (int i=0; i<vars.length; i++) {
            vars[i].dotEnv(dot);
        }
    }
}
