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

package compiler;

/** A simple implementation of the Handler interface that prints the
 *  position and description of each diagnostic on System.err, and
 *  then returns to the caller.
 */
public class SimpleHandler extends Handler {
    /** Respond to a diagnostic by displaying it on the error output
     *  stream.
     */
    protected void respondTo(Diagnostic diagnostic) {
        if (diagnostic instanceof Warning) {
            System.err.print("WARNING: ");
        } else {
            System.err.print("ERROR: ");
        }
        Position pos = diagnostic.getPos();
        if (pos!=null) {
            System.err.println(pos.describe());
        }
        System.err.println(diagnostic.getText());
    }
}
