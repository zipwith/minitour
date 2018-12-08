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

package lexer;

public interface MiniTokens {
    // The following names are used to represent tokens that are (or
    // can be) more than one character long.  Every token should have
    // a distinct code, and these codes should not conflict with the
    // single character token codes listed below.  In all other regards,
    // however, the mapping of tokens to codes is arbitrary.
    public int ENDINPUT = 0;  // Signals the end of the input stream
    public int ID       = 1;  // An identifier
    public int INTLIT   = 2;  // An integer literal
    public int EQEQ     = 3;  // ==  (equality test)
    public int NEQ      = 4;  // !=  (not equals)
    public int LTE      = 5;  // <=  (less than or equal)
    public int GTE      = 6;  // >=  (greater than or equal)
    public int LAND     = 7;  // &&  (logical/short-circuiting and)
    public int LOR      = 8;  // ||  (logical/short-circuiting or)
    public int IF       = 9;  // keyword if
    public int ELSE     = 10; // keyword else
    public int WHILE    = 11; // keyword while
    public int PRINT    = 12; // keyword print
    public int INT      = 13; // keyword int
    public int BOOLEAN  = 14; // keyword boolean
    public int TRUE     = 15; // Boolean literal true
    public int FALSE    = 16; // Boolean literal false

    // The following single characters are also used as token codes:
    //   ';', '{', '}', '=', '(', ')', '(', ')', '+', '-',
    //   '~', '!', '*', '/', '<', '>', '&', '|', '^', ','.
    // All of these characters have distinct numeric codes that are
    // greater than 32, so there is no conflict with the symbolic
    // token code names listed above.
}
