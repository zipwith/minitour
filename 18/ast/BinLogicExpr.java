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

/** Abstract syntax for binary logical expressions.
 */
public abstract class BinLogicExpr extends BinExpr {

    /** Default constructor.
     */
    public BinLogicExpr(Position pos, Expr left, Expr right) {
        super(pos, left, right);
    }

    /** Run type checking analysis on this expression.  The typing parameter
     *  provides access to the scope analysis phase (in particular,
     *  to the associated error handler), and the env parameter
     *  reflects the environment in which the expression is evaluated.
     *  Unlike scope analysis for statements, there is no return
     *  result here: an expression cannot introduce new variables in
     *  to a program, so the final environment will always be the same
     *  as the initial environment.
     */
    public Type analyze(TypeAnalysis typing) {  // LAnd, LOr
        left.require(typing, Type.BOOLEAN);
        right.require(typing, Type.BOOLEAN);
        return type = Type.BOOLEAN;
    }

    /** Run initialization analysis on this expression.  The init parameter
     *  provides access to the initialization analysis phase (in particular,
     *  to the associated error handler), and the initialized parameter
     *  reflects the set of variables that are known to have been initialized
     *  before this expression is evaluated.  The return result is the set of
     *  variables that are known to be initialized after the expression has
     *  been evaluated.  Because there are no side-effecting operations in
     *  the mini language described here, the return result is actually the
     *  same as the input value for initialized in all cases.  But, of
     *  course, this could change in future if new constructs were added to
     *  the language ...
     */
    public VarSet analyze(InitAnalysis init, VarSet initialized) {
        // We override this method in a special way for the logical Boolean
        // operators && and || because these do not always evaluate their
        // right argument.  As a result, we cannot be sure that initializations
        // in the right argument will be reached.  (Not that this matters right
        // now because our language does not currently include side effecting
        // expressions ... but getting the correct logic here now will help us
        // if we decide to extend the language later on.)
  
        initialized = left.analyze(init, initialized);
        right.analyze(init, initialized); // final result is discarded
        return initialized;
    }
}
