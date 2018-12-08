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

/** Abstract syntax for binary expressions.
 */
public abstract class BinExpr extends Expr {

    /** The left subexpression.
     */
    protected Expr left;

    /** The right subexpression.
     */
    protected Expr right;

    /** Default constructor.
     */
    public BinExpr(Position pos, Expr left, Expr right) {
        super(pos);
        this.left = left;
        this.right = right;
    
        // Compute the depth of this expression:
        depth = 1 + Math.max(left.getDepth(), right.getDepth());
    }

    /** Return a string that provides a simple description of this
     *  particular type of operator node.
     */
    abstract String label();

    /** Print an indented description of this abstract syntax node,
     *  including a name for the node itself at the specified level
     *  of indentation, plus more deeply indented descriptions of
     *  any child nodes.
     */
    public void indent(IndentOutput out, int n) {
        out.indent(n, label());
        left.indent(out, n+1);
        right.indent(out, n+1);
    }

    /** Print out this binary expression.
     */
    protected void binary(TextOutput out, String op) {
        left.parenPrint(out);
        out.print(op);
        right.parenPrint(out);
    }

    /** Print out this expression, wrapping it in parentheses if the
     *  expression includes a binary or unary operand.
     */
    public void parenPrint(TextOutput out) {
        out.print("(");
        this.print(out);
        out.print(")");
    }

    /** Output a description of this node (with id n) in dot format,
     *  adding an extra node for each subtree.
     */
    public int toDot(DotOutput dot, int n) {
        return right.toDot(dot, n, "right",
               left.toDot(dot, n, "left",
               node(dot, label(), n)));
    }

    /** Run scope analysis on this expression.  The scoping parameter
     *  provides access to the scope analysis phase (in particular,
     *  to the associated error handler), and the env parameter
     *  reflects the environment in which the expression is evaluated.
     *  Unlike scope analysis for statements, there is no return
     *  result here: an expression cannot introduce new variables in
     *  to a program, so the final environment will always be the same
     *  as the initial environment.
     */
    public void analyze(ScopeAnalysis scoping, Env env) {
        left.analyze(scoping, env);
        right.analyze(scoping, env);
    }

    /** Check that two type arguments, corresponding to the types of a
     *  binary operator's arguments, are equal and report an error if
     *  they differ.
     */
    void requireSame(TypeAnalysis typing, Type lt, Type rt) {
        if (lt!=rt) {
          typing.report(new Failure(pos, "Types of left operand (" + lt +
                                         ") and right operand (" + rt +
                                         ") do not match"));
        }
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
        return right.analyze(init, left.analyze(init, initialized));
    }

    /** Rewrite this expression using algebraic identities to reduce
     *  the amount of computation that is required at runtime.  The
     *  algorithms used here implement a range of useful optimizations
     *  including, for example:
     *     x + 0  ==>  x
     *     n + m  ==>  (n+m)           if n,m are known integers
     *     (x + n) +m ==>  x + (n+m)   if n,m are known integers
     *  etc. with corresponding rules for *, &, |, and ^.  However,
     *  there are still plenty of other opportunities for simplification,
     *  including:
     *    use of identities/constant folding on Booleans
     *    removing double negations, complements, etc...
     *    distributivity properties, such as (x+n)+(y+m) ==> (x+y)+(n+m)
     *    and so on ...
     */
    Expr simplify() {
        left            = left.simplify();
        right           = right.simplify();
        IntLit leftInt  = left.isIntLit();
        IntLit rightInt = right.isIntLit();
        if (leftInt==null) {
            return (rightInt==null) ? this : this.simpR(rightInt.getNum());
        } else {
            return (rightInt==null) ? this.simpL(leftInt.getNum())
                                    : this.fold(leftInt.getNum(),
                                                rightInt.getNum());
        }
    }

    /** Constant folding for binary operators with two known integer
     *  arguments.
     */
    Expr fold(int n, int m) { return this; }

    /** Simplification of a binary expression when the left operand
     *  (but not the right) is a known integer constant.
     */
    Expr simpL(int m) {
        // Default behavior is to leave expression unchanged.
        return this;
    }

    /** Simplification of a binary expression when the right operand
     *  (but not the left) is a known integer constant.
     */
    Expr simpR(int m) { return this; }

    /** Return the depth of this expression as a measure of how complicated
     *  the expression is / how many registers will be needed to evaluate it.
     */
    int getDepth() {
        // Return the depth value that was computed by the constructor
        return depth;
    }

    /** Records the depth of this expression; this value is computed
     *  at the time the constructor is called and then saved here so
     *  that it can be accessed without further computation later on.
     */
    protected int depth;

    /** Generate code to evalute both of the expressions left and right,
     *  changing the order of evaluation if possible/beneficial to
     *  reduce the number of registers that are required.  The return
     *  boolean indicates the order in which the two expressions have
     *  been evaluated and stored in registers.  A true result indicates
     *  that reg(free) contains the value of left and reg(free+1) contains
     *  the value of right.  A false result indicates that the order has
     *  been reversed.  In both cases, reg(free+1) will need to be
     *  unspilled once the value in that register has been used.
     */
    boolean compileBin(IA32 a, Expr left, Expr right, int pushed, int free) {
        if (left.getDepth()>right.getDepth() || right.getDepth()>=DEEP) {
            left.compileExpr(a, pushed, free);
            pushed += a.spill(free+1);
            right.compileExpr(a, pushed, free+1);
            return true;
        } else {
            right.compileExpr(a, pushed, free);
            pushed += a.spill(free+1);
            left.compileExpr(a, pushed, free+1);
            return false;
        }
    }

    /** Generate code to evaluate a binary expression using the specified
     *  opcode, op, to combine the results of the two subexpressions.  If
     *  the order in which the arguments is reversed to reduce register
     *  pressure, then an additional exchange instruction is emitted to
     *  restore the correct order.
     */
    void compileOp(IA32 a, String op, int pushed, int free) {
        if (!compileBin(a, left, right, pushed, free)) {
            a.emit("xchgl", a.reg(free+1), a.reg(free));
        }
        a.emit(op, a.reg(free+1), a.reg(free));
        a.unspill(free+1);
    }

    /** A variant of compileOp that can be used when the operation that is
     *  being performed on the two subexpressions is commutative; in this
     *  case, there is no need to insert an exchange instruction, even if
     *  the order of evaluation was reversed.
     */
    void compileCommutativeOp(IA32 a, String op, int pushed, int free) {
        compileBin(a, left, right, pushed, free);
        a.emit(op, a.reg(free+1), a.reg(free));
        a.unspill(free+1);
    }

    /** Generate code for a comparision operation.  The resulting
     *  code evaluates both left and right arguments, and then does
     *  a comparision, setting the flags ready for the appropriate
     *  conditional jump.  The free+1 register is both spilled and
     *  unspilled in this code, which means that the caller does
     *  not need to handle spilling.
     */
    void compileCond(IA32 a, int pushed, int free) {
        if (compileBin(a, left, right, pushed, free)) {
            a.emit("cmpl", a.reg(free+1), a.reg(free));
        } else {
            a.emit("cmpl", a.reg(free), a.reg(free+1));
        }
        a.unspill(free+1);
     }

    /** Generate code for a comparison that computes either 1 (for
     *  true) or 0 (for false) in the specified free register.  The
     *  given "test" instruction is used to trigger a branch in the
     *  true case.
     */
    void compileCondValue(IA32 a, String test, int pushed, int free) {
        String lab1 = a.newLabel();  // jump here if true
        String lab2 = a.newLabel();  // jump here when done
        compileCond(a, pushed, free);// compare the two arguments
        a.emit(test, lab1);          // jump if condition is true
        a.emit("movl", a.immed(0), a.reg(free));
        a.emit("jmp",  lab2);
        a.emitLabel(lab1);
        a.emit("movl", a.immed(1), a.reg(free));
        a.emitLabel(lab2);           // continue with value in free
    }
}
