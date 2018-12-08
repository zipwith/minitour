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
 *  abstract syntax trees using indentation.
 */
public class HTMLOutput extends TextOutput {

    /** Default constructor.
     */
    public HTMLOutput(java.io.PrintStream out) {
        super(out);
    }

    /** Construct a version of this HTML output that will leave its output
     *  in the named file.
     */
    public HTMLOutput(String filename)
      throws Exception {
        this(new java.io.PrintStream(filename));
    }

    /** Create an HTML pretty printed output of the given program
     *  on the specified output file.
     */
    public void toHTML(Stmt stmt) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>My Pretty-Printed Mini Program</title>");
        out.println("<style type=\"text/css\">");
        out.println("  body     {white-space:pre;");
        out.println("            background-color:#ffffcc;");
        out.println("            color:black;");
        out.println("            font-family:\"Lucida Console\",\"Courier New\",Monotype}");
        out.println("  .normalId    {background-color:transparent}");
        out.println("  .defId       {color:red}");
        out.println("  .useId       {color:blue}");
        out.println("  .highlightId {background-color:lightblue}");
        out.println("</style>");
        out.println("</head>");
        out.println("<script language=\"javascript\">");
        out.println("function change(n,m,s) {");
        out.println("  elem=document.getElementById(\"(\"+n+\", \"+m+\")\");");
        out.println("  elem.className=s;");
        out.println("}");
        out.println("function highlightId(n, m) { change(n,m,\"highlightId\"); }");
        out.println("function defId(n, m)       { change(n,m,\"defId\"); }");
        out.println("function useId(n, m)       { change(n,m,\"useId\"); }");
        out.println("function normalId(n, m)    { change(n,m,\"normalId\"); }");
        out.println("</script>");
        out.println("<body>");

        stmt.printProgram(this);

        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    /** Print an identifier in a context where it is being used
     *  rather than defined.
     */
    public void printUse(Id id) { id.printUseHTML(this); }

    /** Print an identifier in a context where it is being defined
     *  rather than used.
     */
    public void printDef(Id id) { id.printDefHTML(this); }
}
