/*
 * Class:       CS 4308 Section 02
 * Term:        Spring 2022
 * Name:        Kainuo He
 * Instructor:  Sharon Perry
 * Project:     Julia Programming Language Interpreter
 */

final class Token {

    //declare variables
    Kinds  kind_;
    String value_;
    private Token left;
    private Token right;
    private static final Token empty_   = new Token("", Kinds.Empty);                // empty token
    private static final Token unknown_ = new Token("**Unknown**", Kinds.Unknown);   // unknown token


    //declare all kind's name
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

    //default constructor
    public Token(String s, Kinds k) {
        kind_  = k;
        value_ = s;
    }

    //create nextLine token
    static Token nextLine(){
        return new Token("", Kinds.NextLine);
    }


    //transform a token to a identifier token
    Token createID(){
        return new Token(value_, Kinds.Id);
    }


    //return with the appropriate value_ and kind_ of a char
    static Token create(char c) {
        final String s = Character.toString(c);
        if (c>='0'&& c<='9') return new Token(s, Kinds.Integer);
        switch(c) {
            case '<'  : return new Token(s, Kinds.Less              );
            case '>'  : return new Token(s, Kinds.Greater           );
            case '&'  : return new Token(s, Kinds.Ampersand         );
            case '='  : return new Token(s, Kinds.Assign            );
            case '+'  : return new Token(s, Kinds.Plus              );
            case '-'  : return new Token(s, Kinds.Minus             );
            case '*'  : return new Token(s, Kinds.Asterisk          );
            case '/'  : return new Token(s, Kinds.Slash             );
            case ','  : // down through
            case ' '  : return new Token(s, Kinds.Separator         );
            case '('  : return new Token(s, Kinds.LeftParenthesis   );
            case ')'  : return new Token(s, Kinds.RightParenthesis  );
            case '{'  : return new Token(s, Kinds.LeftCurlyBracket  );
            case '}'  : return new Token(s, Kinds.RightCurlyBracket );
            case '['  : return new Token(s, Kinds.LeftSquareBracket );
            case ']'  : return new Token(s, Kinds.RightSquareBracket);
            case ':'  : return new Token(s, Kinds.Colon             );
            case ';'  : return new Token(s, Kinds.SemiColon         );
            case '\\' : return new Token(s, Kinds.BackSlash         );
            case '\"' : return new Token(s, Kinds.DoubleQuote       );
            case '\'' : return new Token(s, Kinds.SingleQuote       );
        }
        return unknown_;
    }

    //return with the appropriate value_ and kind_ of a string
    static Token create(String s) {
        if ( s == null  ||  s.trim().isEmpty() ) { return empty_; }
        if ( s.length() == 1 ) {
            Token t = Token.create(s.charAt(0));
            if ( t.kind() != Kinds.Unknown ) { return t; }
        }
        try {
            // checking if integer using parseInt() method
            Integer.parseInt(s);
            return new Token(s, Kinds.Integer);
        }
        catch (NumberFormatException e) {}
        switch (s){
            case "char"     : return new Token(s, Kinds.Character      );
            case "string"   : return new Token(s, Kinds.String         );
            case "repeat"   : return new Token(s, Kinds.Keyword_Repeat );
            case "until"    : return new Token(s, Kinds.Keyword_Until  );
            case "while"    : return new Token(s, Kinds.Keyword_While  );
            case "do"       : return new Token(s, Kinds.Keyword_Do     );
            case "if"       : return new Token(s, Kinds.Keyword_If     );
            case "then"     : return new Token(s, Kinds.Keyword_Then   );
            case "else"     : return new Token(s, Kinds.Keyword_Else   );
            case "print"    : return new Token(s, Kinds.Keyword_Print  );
            case "end"      : return new Token(s, Kinds.Keyword_End    );
            case "//"       : return new Token(s, Kinds.Comment        );
            case "=="       : return new Token(s, Kinds.Equal          );
            case "<="       : return new Token(s, Kinds.LessEqual      );
            case ">="       : return new Token(s, Kinds.GreaterEqual   );
            case "~="       : return new Token(s, Kinds.NotEqual       );
        }
        return new Token(s.trim(), Kinds.String_Literal);
    }




    final String value()     { return value_; }
    final Kinds  kind()      { return kind_; }
    final String kindName()  { return kind_.toString(); }
    public String toString() {
        return String.format("[%-16s: \"%s\"]", kindName(), value());
    }


}