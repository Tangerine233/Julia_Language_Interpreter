
# Julia_Language_Interpreter

Project deliverable is 100% complete

## Author:
- [@KainuoHe](https://github.com/Tangerine233)

## Purpose
The Purpose for this project is to practice building an interpreter for a programming Language and to improve the ability to code. So I built an interpreter using Java for the minimal form of the programming language Julia. This minimal form of Julia has only 1 data type, integer, and the only identifiers are single letters. 

## Scope
This interpreter is in Java 13.0.6 using IntelliJ IDEA 2020.2.4. The project was divided into 3 parts, Scanner(Lexical Analyzer), Parser(Syntax Analyzer), and Interpreter. The Scanner will scan the Julia file and break the codes down into a list of tokens by lookup to the keyword table. The Parser will read the token list from the Scanner and build a parse tree and an identifier table. And lastly, the Interpreter will take the parse tree and identifier table to execute the program.

## Scanner
The Scanner comes with 2 classes, a Token.java that come with a keyword table, and a LexicalAnalyzer.java that scans a “.jl” file by using BufferedReader and analyzes it into a Token list. The Token class stores the kind and value of a token. The value is stored in String while the lookup table for kinds is shown below.
```bash
enum Kinds {
   Id,
   Unknown,
   Empty,
   NextLine,
   Less,               // "<"
   Greater,            // ">"
   LessEqual,          // ">="
   GreaterEqual,       // "<="
   NotEqual,           // "~="
   Ampersand,          // "&"
   Assign,             // "="
   Equal,              // "=="
   Plus,               // "+"
   Minus,              // "-"
   Asterisk,           // "*"
   Slash,              // "/"
   Separator,          // space
   LeftParenthesis,    // "("
   RightParenthesis,   // ")"
   LeftCurlyBracket,   // "{"
   RightCurlyBracket,  // "}"
   LeftSquareBracket,  // "["
   RightSquareBracket, // "]"
   Colon,              // ":"
   SemiColon,          // ";"
   BackSlash,          // "\"
   DoubleQuote,        // """
   SingleQuote,        // "'"
   String_Literal,
   Character,          //char
   String,             //string
   Integer,            //int
   Keyword_Repeat,     //repeat
   Keyword_Until,      //until
   Keyword_While,      //while
   Keyword_Do,         //do
   Keyword_If,         //if
   Keyword_Then,       //then
   Keyword_Else,       //else
   Keyword_Print,      //print
   Keyword_End,        //end
   Comment,            // "//"
}
```

## Parser
The Parser has 2 classes, Node.java and SyntaxAnalyzer.java. The Node class store the nodeName in string, a token, and an array list of Nodes for kids. And the SyntaxAnalyzer will read an analyzed LexcicalAnalyzer, then generate an identifier table using HashMap and build the parse tree by the following syntax.

```bash
<program> → function id ( ) <block> end

<block> → <statement> | <statement> <block>

<statement> → <if_statement> | <assignment_statement> | <while_statement> |
<print_statement> | <repeat_statement>

<if_statement> → if <boolean_expression> then <block> else <block> end

<while_statement> → while <boolean_expression> do <block> end

<assignment_statement> → id <Assign> <arithmetic_expression>

<repeat_statement> → repeat <block> until <boolean_expression>

<print_statement> → print ( <arithmetic_expression> )

<boolean_expression> → <relative_op> <arithmetic_expression> <arithmetic_expression>

<relative_op> → <Less> | <LessEqual> | <Greater> | <GreaterEqual> | <Equal>
 | <NotEqual>

<arithmetic_expression> → <id> | <literal_integer> 
| <arithmetic_op> <arithmetic_expression> <arithmetic_expression>

<arithmetic_op> → <Plus> | <Minus> | <Asterisk> | <Slash>
```
## Interpreter
The Interpreter has one class named Interpreter.java. The interpreter integrates the scanner and parser, and execute the program. It takes a file in BufferedReader and pass it to the lexical analyzer to analyze, and pass it to the syntax analyzer, then take the identifier table and the parse tree to execute. It has the constructor and the major functions as following.
Interpreter(BufferedReader file) - Constructor build by a BufferedReader
printTokens() - print out all the tokens
printParseTree() - print out the parse tree
run() - execute the Julia codes


## Sample Julia File
File Test3.jl content:

```bash
//Test 3 in Julia

function a()
	x = 1
		if ~= x 1 then
			print(0)
		else
			print(1)
		end
end
```

## Sample Main
```bash
import java.io.BufferedReader;
import java.io.FileReader;

public class main {
    public static void main(String[] args) throws Exception {

        BufferedReader file = new BufferedReader(new FileReader(System.getProperty("user.dir") + 
                "\\TestJuliaScripts\\" + "Test3.jl"));
        Interpreter in = new Interpreter(file);

        System.out.println("\n\n***********************Token List***********************");
        in.printTokens();
        System.out.println("\n\n***********************Parse Tree***********************");
        in.printParseTree();
        System.out.println("\n\n***********************Execution***********************");
        in.run();

        file.close();
    }
}
```

## Sample Output
```bash
*************Token List*************
[NextLine        : ""]
[NextLine        : ""]
[String_Literal  : "function"]
[String_Literal  : "a"]
[LeftParenthesis : "("]
[RightParenthesis: ")"]
[NextLine        : ""]
[String_Literal  : "x"]
[Assign          : "="]
[Integer         : "1"]
[NextLine        : ""]
[Keyword_If      : "if"]
[NotEqual        : "~="]
[String_Literal  : "x"]
[Integer         : "1"]
[Keyword_Then    : "then"]
[NextLine        : ""]
[Keyword_Print   : "print"]
[LeftParenthesis : "("]
[Integer         : "0"]
[RightParenthesis: ")"]
[NextLine        : ""]
[Keyword_Else    : "else"]
[NextLine        : ""]
[Keyword_Print   : "print"]
[LeftParenthesis : "("]
[Integer         : "1"]
[RightParenthesis: ")"]
[NextLine        : ""]
[Keyword_End     : "end"]
[NextLine        : ""]
[Keyword_End     : "end"]
[NextLine        : ""]


*************Parse Tree*************
<program> :: function <id> ( ) <block> end 
<id> :: a 
<block> :: <statement> <statement> 
<statement> :: <assign_statement> 
<assign_statement> :: <id> = <arithmetic_expression> 
<id> :: x 
<arithmetic_expression> :: <Integer> 
<Integer> :: 1 
<statement> :: <if_statement> 
<if_statement> :: if <boolean_expression> then <block> else <block> end 
<boolean_expression> :: <relate_op> <arithmetic_expression> <arithmetic_expression> 
<relate_op> :: ~= 
<arithmetic_expression> :: <id> 
<id> :: x 
<arithmetic_expression> :: <Integer> 
<Integer> :: 1 
<block> :: <statement> 
<statement> :: <print_statement> 
<print_statement> :: print ( <arithmetic_expression> ) 
<arithmetic_expression> :: <Integer> 
<Integer> :: 0 
<block> :: <statement> 
<statement> :: <print_statement> 
<print_statement> :: print ( <arithmetic_expression> ) 
<arithmetic_expression> :: <Integer> 
<Integer> :: 1 


*************Execution*************
1

Process finished with exit code 0
```
