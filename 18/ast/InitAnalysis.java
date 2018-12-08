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
import compiler.Failure;
import compiler.Handler;
import compiler.Phase;

/** Represents a static analysis phase that performs initialization
 *  analysis, assuming a previous (and successful) use of scope analysis.
 */
public class InitAnalysis extends Phase {

    /** Default constructor.
     */
    public InitAnalysis(Handler handler) {
        super(handler);
    }

    /** Run initialization analysis on the specified statement, assuming
     *  an empty set of already initialized variables.
     */
    public void analyze(Stmt stmt)
      throws Failure {
        stmt.analyze(this, null);
        if (getHandler().hasFailures()) {
            throw new Failure("Aborting: errors detected during initialization analysis");
        }
    }
}
