/*
 * Class:       CS 4308 Section 02
 * Term:        Spring 2022
 * Name:        Kainuo He
 * Instructor:  Sharon Perry
 * Project:     Julia Programming Language Interpreter
 */

import java.io.BufferedReader;  //provide BufferedReader
import java.util.*;             //provide ArrayList

public class LexicalAnalyzer {

    int lineCount = 0;
    final BufferedReader target_;       // analyze target string
    String line;                       // hold targets_file line by line
    int length_=0;                     // length of target string
    int pos_ = 0;                      // "next" analyzing position

    List<Token> tokens_ = new ArrayList<>();

    //constructors
    private LexicalAnalyzer(BufferedReader filename) throws Exception {
        target_ = filename;
    }


    //create new lexical analyzer if the target is not null or empty
    public static LexicalAnalyzer create(BufferedReader target) throws Exception {
        return new LexicalAnalyzer(target);
    }


    //skips spaces and returns nextAll();
    private char next() {
        skipSpace();
        return nextAll();
    }

    //read new characters and advances pos_ by 1
    private char nextAll() {
        char c = aChar();
        pos_++;
        return c;
    }

    //return character at the the current reading position
    private char aChar() {
        if (isEnd()) return '\0';
        else return line.charAt(pos_);
    }

    //skip all empty spaces and advances pos_
    private void skipSpace() {
        while ( !isEnd()  &&  Character.isWhitespace(aChar()) ) {
            pos_++;
        }
    }

    //check is current character end, separator, quote, symbol, or white space
    private boolean isEnd() { return length_ <= pos_; }
    private boolean isSeparator(char c)     { return exists(separators_, c);    }
    private boolean isQuote(char c)         { return exists(quotes_, c);        }
    private boolean isSymbol_1(char c)      { return exists(symbol1_, c);       }
    private boolean isWhitespace(char c)    { return Character.isWhitespace(c); }



    //search to see if match the symbol
    private boolean exists(char[] arr, char c) {
        return Arrays.binarySearch(arr, c) >= 0;
    }
    private static final char[] separators_ = {  ',', '=', '(', ')', '{', '}', ':' };
    static { Arrays.sort(separators_); }

    private static final char[] quotes_ = { '"', '\'' };
    static { Arrays.sort(quotes_); }

    private static final char[] symbol1_ = {'~','<','>', '(', ')', '{', '}', ':', ',', '=', '&' ,'+','-'};

    static { Arrays.sort(symbol1_); }


    //string(identifier) processing
    private void text(char first) {
        StringBuilder builder = new StringBuilder();
        builder.append(first);
        char c;
        while ( (c = nextAll()) != '\0'  &&  !isSeparator(c)  &&  !Character.isWhitespace(c) ) {
            builder.append(c);
        }
        tokens_.add( Token.create(builder.toString()) ); // append string


        //if follow by non string
        if (isWhitespace(c)) tokens_.add(Token.create(' '));
        else if (isSeparator(c)) tokens_.add(Token.create(c));
    }


    //string processing with quote
    private void quotedText(char quote) {
        tokens_.add( Token.create(quote));  // create token of begin quote

        StringBuilder builder = new StringBuilder();
        char c;
        while ( (c = nextAll()) != '\0'  &&  c != quote) { builder.append(c); }

        if ( builder.length() != 0 ) {
            tokens_.add( Token.create(builder.toString()) );  // append string
        }

        tokens_.add( Token.create(c) );  // append token of end quote
    }



    //Analysis Processing
    public List<Token> analyze() throws Exception {
        char c = 0;
        line = target_.readLine();
        //while not EOF
        while (line!=null) {
            length_ = line.length();
            pos_ = 0;

            //read the target line
            while (!isEnd()) { //get one character each time to be analyzed
                //skip comment
                if (pos_+1<length_ && line.charAt(pos_)=='/' && line.charAt(pos_+1)=='/'){
                    pos_=length_;
                    break;
                }
                c=next();

                //System.out.println("c="+c);
                if (isSymbol_1(c)) {
                    //two char symbols
                    if (next()=='='){
                        switch (c){
                            case '<':tokens_.add(Token.create("<="));break;
                            case '>':tokens_.add(Token.create(">="));break;
                            case '~':tokens_.add(Token.create("~="));break;
                            case '=':tokens_.add(Token.create("=="));break;
                            default:tokens_.add(Token.create(c));
                        }
                    }
                    else {
                        tokens_.add(Token.create(c));
                        pos_--;
                    }
                    continue;
                }
                else if (isQuote(c)) {
                    quotedText(c);
                    continue;
                }
                text(c);

            }


            line = target_.readLine();
            tokens_.add(Token.nextLine());
            lineCount++;
        }


        List<Token> newToks = new ArrayList<>();
        for (int i =0; i<tokens_.size();i++){
            if (tokens_.get(i).kind() != Token.Kinds.Separator){

                newToks.add(tokens_.get(i));
            }
        }
        tokens_=newToks;

        return tokens_;
    }



    //returns true if the token list of the analysis results is empty
    public boolean isEmpty() { return tokens_.size() == 0;}
    //checks the results for incorrect tokens, can't be empty and no unknown
    public boolean isValid() {
        return !isEmpty()  &&  tokens_.stream().noneMatch( e -> e.kind() == Token.Kinds.Unknown );
    }



}




