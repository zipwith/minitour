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

/** Represents an output phase for producing "pretty-printed" textual
 *  output of abstract syntax trees using indentation.  Whether or not
 *  the output actually matches anyone's notion of "pretty" is another
 *  matter of course!  (It might help if the output also included
 *  comments, but this phase operates on the output of the parser,
 *  by which point the comments have already been discarded during
 *  lexical analysis.)
 */
public class TextOutput {

    protected java.io.PrintStream out;

    /** Default constructor.
     */
    public TextOutput(java.io.PrintStream out) {
        this.out = out;
    }

    /** Construct a version of this Text output that will leave its output
     *  in the named file.
     */
    public TextOutput(String filename)
      throws Exception {
        this(new java.io.PrintStream(filename));
    }

    /** Construct a TextOutput object that will display its output on the
     *  standard output.
     */
    public TextOutput()
      throws Exception {
        this(System.out);
    }

    /** Create an Text pretty printed output of the given program
     *  on the specified output file.
     */
    public void toText(Stmt stmt) {
        stmt.printProgram(this);
    }

    /** Indent the output stream according to the specified nesting level n.
     */
    public void indent(int n) {
        for (int i=0; i<n; i++) {
            out.print("    ");
        }
    }

    /** Indent to the specified level and then print a string.
     */
    public void indent(int n, String s) { indent(n); print(s); }

    /** Print a string on the output stream.
     */
    public void print(String s) { out.print(s); }

    /** Print a string followed by a newline on the output stream.
     */
    public void println(String s) { out.println(s); }

    /** Print a newline on the output stream.
     */
    public void println() { out.println(); }
}
