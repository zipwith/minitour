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
        String name = id.getName();
        while (env!=null && !name.equals(env.id.getName())) {
            env = env.rest;
        }
        return env;
    }
}
