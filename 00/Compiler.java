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

// 00 Simple command line program with arguments
public class Compiler {
  public static void main(String[] args) {
    if (args.length!=1) {
      System.out.println("This program requires exactly one argument");
    } else {
      System.out.println("Ok, we should look for an input called " + args[0]);
    }
  }
}
