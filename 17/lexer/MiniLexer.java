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

import java.util.Hashtable;
import compiler.Source;
import compiler.SourceLexer;
import compiler.Handler;
import compiler.Warning;
import compiler.Failure;

/** A lexical analyzer.
 */
public class MiniLexer extends SourceLexer implements MiniTokens {
    /** Construct a lexical analyzer.
     */
    public MiniLexer(Handler handler, Source source) {
        super(handler, source);
    }

    //- Main lexical analysis: ------------------------------------------------

    /** Read the next token and return the corresponding integer code.
     */
    public int nextToken() {
/*
       System.out.println("token: " + mynextToken());
       return token;
    }

    public int mynextToken() {
*/
        for (;;) {
            skipWhitespace();
            markPosition();
            switch (c) {
                case EOF  : return token=ENDINPUT;

                // Separators:
                case '('  : nextChar();
                            return token='(';
                case ')'  : nextChar();
                            return token=')';
                case '{'  : nextChar();
                            return token='{';
                case '}'  : nextChar();
                            return token='}';
                case ';'  : nextChar();
                            return token=';';
                case ','  : nextChar();
                            return token=',';

                // Operators:
                case '='  : nextChar();
                            if (c=='=') {
                                nextChar();
                                return token=EQEQ;
                            } else {
                                return token='=';
                            }

                case '!'  : nextChar();
                            if (c=='=') {
                                nextChar();
                                return token=NEQ;
                            } else {
                                return token='!';
                            }

                case '<'  : nextChar();
                            if (c=='=') {
                                nextChar();
                                return token=LTE;
                            } else {
                                return token='<';
                            }

                case '>'  : nextChar();
                            if (c=='=') {
                                nextChar();
                                return token=GTE;
                            } else {
                                return token='>';
                            }

                case '&'  : nextChar();
                            if (c=='&') {
                                nextChar();
                                return token=LAND;
                            } else {
                                return token='&';
                            }

                case '|'  : nextChar();
                            if (c=='|') {
                                nextChar();
                                return token=LOR;
                            } else {
                                return token='|';
                            }

                case '^'  : nextChar();
                            return token='^';

                case '~'  : nextChar();
                            return token='~';

                case '+'  : nextChar();
                            return token='+';

                case '-'  : nextChar();
                            return token='-';

                case '*'  : nextChar();
                            return token='*';

                case '/'  : nextChar();
                            if (c=='/') {
                                skipOneLineComment();
                            } else if (c=='*') {
                                skipBracketComment();
                            } else {
                                return token = '/';
                            }
                            continue;

                default   : if (Character.isJavaIdentifierStart((char)c)) {
                                return identifier();
                            } else if (Character.digit((char)c, 10)>=0) {
                                return number();
                            }
            }
            illegalCharacter();
            nextChar();
        }
    }

    //- Whitespace and comments -----------------------------------------------

    private boolean isWhitespace(int c) {
        return (c==' ') || (c=='\t') || (c=='\f');
    }

    private void skipWhitespace() {
        while (isWhitespace(c)) {
            nextChar();
        }
        while (c==EOL) {
            nextLine();
            while (isWhitespace(c)) {
                nextChar();
            }
        }
    }

    private void skipOneLineComment() { // Assumes c=='/'
        nextLine();
    }

    private void skipBracketComment() { // Assumes c=='*'
        nextChar();
        for (;;) {
            if (c=='*') {
                do {
                    nextChar();
                } while (c=='*');
                if (c=='/') {
                    nextChar();
                    return;
                }
            }
            if (c==EOF) {
                report(new Failure(getPos(), "Unterminated comment"));
                return;
            }
            if (c==EOL) {
                nextLine();
            } else {
                nextChar();
            }
        }
    }

    //- Identifiers, keywords, boolean and null literals ----------------------

    private int identifier() {          // Assumes isJavaIdentifierStart(c)
        int start = col;
        do {
            nextChar();
        } while (c!=EOF && Character.isJavaIdentifierPart((char)c));
        lexemeText = line.substring(start, col);

        Integer kw = reserved.get(lexemeText);
        if (kw!=null) {
            return token=kw.intValue();
        }
        return token=ID;
    }

    private static Hashtable<String, Integer> reserved;
    static {
        reserved = new Hashtable<String, Integer>();
        reserved.put("int",     new Integer(INT));
        reserved.put("boolean", new Integer(BOOLEAN));
        reserved.put("if",      new Integer(IF));
        reserved.put("else",    new Integer(ELSE));
        reserved.put("while",   new Integer(WHILE));
        reserved.put("print",   new Integer(PRINT));
        reserved.put("true",    new Integer(TRUE));
        reserved.put("false",   new Integer(FALSE));
    }

    //- Numeric integer literals ----------------------------------------------

    /** Records the numeric value of the most recently read integer literal.
     */
    public int num = 0;

    /** Return the numeric value of the most recently read integer literal.
     */
    public int getNum() {
        return num;
    }

    /** Read an integer literal.
     */
    private int number() {              // Assumes c is a digit
        num   = 0;
        int d = Character.digit((char)c, 10);
        do {
            num = 10*num + d;
            nextChar();
            d = Character.digit((char)c, 10);
        } while (d>=0);
        return token=INTLIT;
    }

    //- Display token name (for debugging purposes) ---------------------------

    public String tokenName() {
      switch (token) {
        case ENDINPUT : return "end of input";
        case '('      : return "open parenthesis";
        case ')'      : return "close parenthesis";
        case '{'      : return "open brace";
        case '}'      : return "close brace";
        case ';'      : return "semicolon";
        case ','      : return "comma";
        case '='      : return "= operator";
        case EQEQ     : return "== operator";
        case NEQ      : return "!= operator";
        case '<'      : return "< operator";
        case LTE      : return "<= operator";
        case '>'      : return "> operator";
        case GTE      : return ">= operator";
        case '+'      : return "+ operator";
        case '-'      : return "- operator";
        case '*'      : return "* operator";
        case '/'      : return "/ operator";
        case '&'      : return "& operator";
        case '^'      : return "^ operator";
        case '|'      : return "| operator";
        case LAND     : return "&& operator";
        case LOR      : return "|| operator";
        case '!'      : return "! operator";
        case '~'      : return "~ operator";
        case IF       : return "if keyword";
        case ELSE     : return "else keyword";
        case WHILE    : return "while keyword";
        case PRINT    : return "print keyword";
        case INT      : return "int keyword";
        case BOOLEAN  : return "boolean keyword";
        case ID       : return "identifier, " + lexemeText;
        case INTLIT   : return "integer literal, " + num;
        case TRUE     : return "Boolean literal, true";
        case FALSE    : return "Boolean literal, false";
        default       : return "token (code " + token + ")";
      }
    }

    //- Error reporting: ------------------------------------------------------

    private void illegalCharacter() {
        report(new Warning(getPos(), "Ignoring illegal character"));
    }
}
