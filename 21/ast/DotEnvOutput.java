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

/** Represents an output phase for producing descriptions of
 *  environments in dot format, suitable for the AT&T
 *  Graphviz tools.
 */
public class DotEnvOutput {

    protected java.io.PrintStream out;

    public DotEnvOutput(java.io.PrintStream out) {
        this.out = out;
    }

    public DotEnvOutput(String filename)
      throws Exception {
        this(new java.io.PrintStream(filename));
    }

    /** Create a dot graph on the specified output stream to
     *  describe the environment for the given statement.
     */
    public void dotEnv(Stmt stmt) {
        out.println("digraph AST {");
        out.println("node [style=filled fontname=Courier fontsize=16];");
        out.println("edge [dir=back];");
        stmt.dotEnv(this);
        out.println("}");
        out.close();
    }

    /** Output a description of a particular node in the
     *  dot description of an Env.
     */
    public void node(int uid, String label) {
        node(uid, label, "lightblue");
    }

    /** Output an edge between the specified pair of Env nodes.
     */
    public void edge(int from, int to) {
        out.println(to + " -> " + from + ";");
    }

    /** Output a description of a particular node in the dot
     *  description of an Env, including color information.
     */
    public void node(int uid, String label, String fillcolor) {
        out.print(uid + "[label=\"" + label
                      + "\" fillcolor=\"" + fillcolor + "\"];");
    }
}
