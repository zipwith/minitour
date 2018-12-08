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

/** Represents an environment that stores information about the
 *  type of each variable in a program.
 */
public class Env {

    /** The identifier for this environment entry.
     */
    private Id id;

    /** The type for this environment entry.
     */
    private Type type;

    /** Enclosing items for this environment entry.
     */
    private Env rest;

    /** Default constructor.
     */
    public Env(Id id, Type type, Env rest) {
        this.id = id;
        this.type = type;
        this.rest = rest;
    
        // Calculate the offset for this variable, relative to the position of
        // the stack pointer after the enclosing function has been entered but
        // before before allocating any space for locals:
        this.ia32Offset = ((rest==null) ? 0 : rest.ia32Offset) - IA32.WORDSIZE;
        if (-this.ia32Offset > ia32Locals) {
            ia32Locals = (-this.ia32Offset);
        }
    }

    /** Return the Id for this environment entry.
     */
    public Id getId() {
        return id;
    }

    /** Return a pointer to the (first) entry for an item with the same
     *  name as identifier id in the given environment, or null if there
     *  are no entries for id.
     */
    public static Env lookup(Id id, Env env) {
        for (String name=id.getName(); env!=null; env=env.rest) {
            if (name.equals(env.id.getName())) {
                env.uses = new IdList(id, env.uses);
                return env;
            }
        }
        return null;
     }

    /** A counter that is used to assign a distinct numeric code
     *  to every environment node that we construct.
     */
    private static int count = 0;

    /** A numeric code that uniquely identifies this environment node.
     */
    private final int uid = count++;

    /** Generate a dot description for the environment structure of this
     *  program.
     */
    public void dotEnv(DotEnvOutput dot) {
        dot.node(uid, id.getName(), Type.color(type));
        if (rest!=null) {
            dot.edge(uid, rest.uid);
        }
    }

    /** Holds a list of all the uses of the identifier that is named in
     *  this environment entry.
     */
    IdList uses = null;

    /** Return the list of uses for this environment entry identifier.
     */
    public IdList getUses() {
        return uses;
    }

    /** Return the type for the variable defined in this environment node.
     */
    public Type getType() {
        return type;
    }

    /** Stores the current value of the variable that is
     *  associated with this environment entry.
     */
    int val = 42;

    /** Return the current value of a variable.
     */
    int load()
      throws Failure { return val; }

    /** Set a new value for a variable.
     */
    void store(int k)
      throws Failure { val = k; }

    /** Holds an offset that indicates where the storage for this variable
     *  will be allocated on the stack.  The first variable is allocated
     *  at offset 0, the next at offset 1, and so on.  Offsets are chosen
     *  so that no two variables that are in scope at the same time have
     *  the same offset.  However, it is possible for distinct variables
     *  with disjoint scopes to share the same offset.
     */
    private int ia32Offset = 0;

    /** Return the stack offset for this variable.  Offsets are chosen
     *  so that no two variables that are in scope at the same time have
     *  the same offset value.
     */
    public int getIa32Offset() {
        return ia32Offset;
    }

    /** Holds the number of bytes that are needed to store the local variables
     *  for this program.
     */
    public static int ia32Locals = 0;

    /** Return the number of bytes that are needed to store the values of the
     *  local variables for this program.
     */
    public static int getIa32Locals() {
        return ia32Locals;
    }
}
