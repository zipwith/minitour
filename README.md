# minitour - a tour through the phases of a compiler for a mini programming language

The files in this repository were originally developed as teaching
materials for the CS321 course on "Languages and Compiler Design"
at Portland State University.  They describe the construction, in
stages, of a compiler for a simple programming language called "mini".
Each folder in this repository contain the Java source code for a
version of the compiler, each of which extends the implementation of
the previous version.  The code is intended to be used together with
the description in the `minitour.pdf` document, which provides
short summaries to explain what is being added at each step.

The different versions of the compiler that are included in the
tour are as follows:

* 00 Simple command line application with arguments

* 01 Using an error handler

* 02 Using exception handling (try, catch, and throw)

* 03 Reading a sequence of integer codes from a file

* 04 Reading a sequence of characters from a file

* 05 Reading a sequence of lines from an input source

* 06 Reading a sequence of tokens from a lexer

* 07 Building a parse tree from a lexer

* 08 Displaying abstract syntax trees using indentation

* 09 Pretty printing abstract syntax trees

* 10 Drawing abstract syntax trees using dot

* 11 Drawing annotated abstract syntax trees using dot

* 12 Scope analysis

* 13 Drawing environments using dot

* 14 Pretty printing to HTML

* 15 Type checking

* 16 Drawing type annotated abstract syntax trees with dot

* 17 Drawing type annotated environments with dot

* 18 Initialization analysis

* 19 Simplification of arithmetic expressions

* 20 Evaluation

* 21 Compilation

