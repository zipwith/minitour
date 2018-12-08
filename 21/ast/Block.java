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

/** A block of statements.
 */
public class Block extends Stmt {

    /** The list of zero or more statements that
     *  make up the body of this block.
     */
    private Stmt[] body;

    /** Default constructor.
     */
    public Block(Stmt[] body) {
        this.body = body;
    }

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, "Block");
        for (int i=0; i<body.length; i++) {
            body[i].indent(out, n+1);
        }
    }

    /** Generate a pretty-printed description of this abstract syntax
     *  node using the concrete syntax of the mini programming language.
     */
    public void print(TextOutput out, int n) {
        out.indent(n);
        out.println("{");
        this.printBlock(out, n+1);
        out.indent(n);
        out.println("}");
    }

    /** Print this statement as the "ifTrue" branch of an if-then-else
     *  having just printed the parenthesized test, but no newline.  This
     *  allows us to override the behaviour for Blocks to match the
     *  desired output formatting.  The elseStmt parameter specifies the
     *  corresponding else statement that should also be printed.  For
     *  current purposes, there are no if-then-else statements that do
     *  not have an else branch.
     */
    public void printThenElse(TextOutput out, int n, Stmt elseStmt) {
        out.println(" {");
        printBlock(out, n+1);
        out.indent(n);
        out.print("} else");
        elseStmt.printElse(out, n);
    }

    /** Print this statement as the "ifFalse" branch of an if-then-else
     *  having just printed the "else" keyword but no newline.  Again,
     *  this allows us to override the behavior for Blocks.
     */
    public void printElse(TextOutput out, int n) {
        out.println(" {");
        printBlock(out, n+1);
        out.indent(n);
        out.println("}");
    }

    /** Print out a program.  In general, we expect a top-level program to
     *  be represented by a Block, whose contents will be displayed here
     *  without the enclosing braces that might otherwise be expected.  We
     *  will also define an implementation of this method for arbitrary
     *  Stmt values too so that it can be used for debugging and testing.
     */
    public void printProgram(TextOutput out) { printBlock(out, 0); out.println(); }

    /** Print the contents of a Block of statements without braces.
     */
    void printBlock(TextOutput out, int n) {
        for (int i=0; i<body.length; i++) {
            body[i].print(out, n);
        }
    }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        int c = dot.node("Block", n);
        for (int i=0; i<body.length; i++) {
            c = body[i].toDot(dot, n, "" + i, c);
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
        Env local = env;
        for (int i=0; i<body.length; i++) {
            local = body[i].analyze(scoping, local);
        }
        return env;
    }

    /** Generate a dot description for the environment structure of this
     *  program.
     */
    public void dotEnv(DotEnvOutput dot) {
        for (int i=0; i<body.length; i++) {
            body[i].dotEnv(dot);
        }
    }

    /** Run type checker on this statement.  The typing parameter
     *  provides access to the scope analysis phase (specifically,
     *  to the associated error handler).
     */
    public void analyze(TypeAnalysis typing) {
        for (int i=0; i<body.length; i++) {
            body[i].analyze(typing);
        }
    }

    /** Run initialization analysis on this statement.  The init
     *  parameter provides access to an initialization analysis phase
     *  object (specifically, to an associated error handler).  The
     *  initialized parameter is the set of variables (each represented
     *  by pointers to environment entries) that have definitely been
     *  initialized before this statement is executed.
     */
    public VarSet analyze(InitAnalysis init, VarSet initialized) {
        for (int i=0; i<body.length; i++) {
            initialized = body[i].analyze(init, initialized);
        }
        return initialized;
    }

    /** Attempt to simplify all of the expressions in this statement.
     */
    public void simplify() {
        for (int i=0; i<body.length; i++) {
            body[i].simplify();
        }
    }

    /** Execute this program.
     */
    public void exec()
      throws Failure {
        for (int i=0; i<body.length; i++) {
            body[i].exec();
        }
    }

    /** Generate code for executing this statement.
     */
    public void compile(IA32 a, int pushed) {
        for (int i=0; i<body.length; i++) {
            body[i].compile(a, pushed);
        }
    }
}
