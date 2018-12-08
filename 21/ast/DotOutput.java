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

/** Represents an output phase for producing textual output of
 *  abstract syntax trees in dot format, suitable for the AT&T
 *  Graphviz tools.
 */
public class DotOutput {

    protected java.io.PrintStream out;

    public DotOutput(java.io.PrintStream out) {
        this.out = out;
    }

    public DotOutput(String filename)
      throws Exception {
        this(new java.io.PrintStream(filename));
    }

    /** Create a dot graph on the specified output stream to
     *  describe the program for a specific source program.
     */
    public void toDot(Stmt stmt) {
        out.println("digraph AST {");
        out.println("node [shape=box style=filled fontname=Courier];");
        stmt.toDot(this, 0);
        out.println("}");
        out.close();
    }

    /** Output a description of a particular node in the
     *  dot description of an AST.  The label specifies the
     *  "friendly" description of the node that will be used
     *  in the output, while the nodeNo is an integer value
     *  that uniquely identifies this particular node.  We
     *  return nodeNo+1 at the end of this function, indicating
     *  the next available node number.
     */
    public int node(String label, String fillcolor, int nodeNo) {
        out.print(nodeNo + "[label=\"" + label
                         + "\" fillcolor=\"" + fillcolor + "\"];");
        return nodeNo+1;
    }

    /** Output a description of a node using a default color scheme.
     */
    public int node(String label, int nodeNo) {
        return node(label, "lightblue", nodeNo);
    }

    /** Output an edge between the specified pair of AST nodes
     *  in the appropriate format for the dot tools.
     */
    public void join(int from, int to, String attr) {
        out.println(from + " -> " + to
           + "[label=\" " + attr + "\", fontcolor=\"gray\"];");
    }
}
