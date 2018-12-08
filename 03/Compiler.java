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

// 03 Reading a sequence of integer codes from a file
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
      FileReader reader = new FileReader(args[0] + ".mini");             // <<<
      int n = 0;  // Number of characters read
      int c;      // Code for individual characters
      while ((c = reader.read()) != -1) {                                // <<<
        System.out.print("| " + c + "\t");
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
}
