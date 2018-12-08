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

// 04 Reading a sequence of characters from a file
import compiler.*;
import java.io.FileReader;

public class Compiler {
  public static void main(String[] args) {
    Handler handler = new SimpleHandler();
    try {
      if (args.length!=1) {
        throw new Failure("This program requires exactly one argument");
      }

      // Read program:
      FileReader reader = new FileReader(args[0] + ".mini");
      int n = 0;  // Number of characters read
      int c;      // Code for individual characters
      while ((c = reader.read()) != -1) {
        System.out.print("| " + charAsString(c) + "\t");                 // <<<
        if ((++n % 8)==0) {
          System.out.println("|");
        }
      }
      System.out.println("|");
        
    } catch (Failure f) {
      handler.report(f);
    } catch (Exception e) { 
      handler.report(new Failure("Exception: " + e));
    }     
  }     

  /** Return a printable string corresponding to a character
   *  with the specified integer code.  A single character
   *  string is returned for the usual Alphabetic, numeric,
   *  and standard symbol characters.  Spaces are represented
   *  by the three character string ' ' to ensure that they
   *  are visible.  Finally, we make a modest attempt to
   *  provide more readable descriptions for the most commonly
   *  used unprintable characters, such as backspaces, tabs,
   *  and newlines, but quickly give
   */
  public static String charAsString(int c) {                             // <<<
    switch (c) {
      case  8 : return "\\b";
      case  9 : return "\\t";
      case 10 : return "\\n";
      case 12 : return "\\f";
      case 13 : return "\\r";
      case 32 : return "' '";
      default : if (c>=32 && c<=127) {
                  return Character.toString((char)c);
                } else {
                  return "?";  // unprintable
                }
    }
  }
}
