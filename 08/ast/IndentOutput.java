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

/** Represents an output phase for producing textual output of
 *  abstract syntax trees using indentation.
 */
public class IndentOutput {

    private java.io.PrintStream out;

    /** Default constructor.
     */
    public IndentOutput(java.io.PrintStream out) {
        this.out = out;
    }

    /** Output an indented description of the abstract syntax
     *  tree for the given statement.
     */
    public void indent(Stmt stmt) {
        stmt.indent(this, 0);
    }

    /** Print a given String message indented some number of
     *  spaces (currently two times the given nesting level, n).
     */
    public void indent(int n, String msg) {
        for (int i=0; i<n; i++) {
            out.print("  ");
        }
        out.println(msg);
    }
}
