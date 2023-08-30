/*
 * Class:       CS 4308 Section 02
 * Term:        Spring 2022
 * Name:        Kainuo He
 * Instructor:  Sharon Perry
 * Project:     Julia Programming Language Interpreter
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyntaxAnalyzer {


    Map<String,Token> id = new HashMap<>();   //identifier table
    List<Token> tokens_;
    int length = 0;
    int lineCount =1;
    private int pos_ = 0;    // for position check
    Node tempNode;
    Node root;


    //constructor
    public SyntaxAnalyzer(LexicalAnalyzer lex){
        tokens_=lex.tokens_;
        pos_=0;
        length = tokens_.size();
    }


    //analyze()
    public Node analyze() throws Exception {
        root = new Node("<program>");
        root = program(root, pos_,length-1);
        return root;
    }


    //<program> → function ID() <block> end
    private Node program(Node paren, int begin, int end) throws Exception {
        //skip empty lines before and after
        while (tokens_.get(begin).kindName().equals("NextLine")) {
            lineCount++;
            begin++;
        }
        while (tokens_.get(end).kindName().equals("NextLine")) {
            end--;
        }
        // "function ID() /n......./n end "
        if (tokens_.get(begin).value().equals("function") &&
            tokens_.get(begin + 1).kindName().equals("String_Literal") &&
            tokens_.get(begin + 2).kindName().equals("LeftParenthesis") &&
            tokens_.get(begin + 3).kindName().equals("RightParenthesis") &&
            tokens_.get(begin + 4).kindName().equals("NextLine") &&
            tokens_.get(end - 1).kindName().equals("NextLine") &&
            tokens_.get(end).value().equals("end"))
        {
            //"function"
            paren.addKid(new Node("function"));
            //create tempNode and transform the String_Literal to an Id
            tempNode = new Node("<id>");
            tempNode.addKid(new Node(tokens_.get(begin+1).createID()));
            //store id in identifier table
            id.put(tokens_.get(begin+1).value(), new Token("0",Token.Kinds.Id));
            //add "id" node to <program>
            paren.addKid(tempNode);

            //add "()" to <program>
            paren.addKid(new Node("("));
            paren.addKid(new Node(")"));
            lineCount++;//ignore the NextLine but still advance lineCount
            //add <block> to <program>
            tempNode = new Node("<block>");
            tempNode = block(tempNode, begin+5, end - 2);
            paren.addKid(tempNode);
            //add "end" to <program>
            paren.addKid(new Node("end"));
        } else {
            System.out.println("Line " + lineCount + " ERROR: Invalid function declaration");
            throw new Exception("Line " + lineCount + " ERROR: Invalid function declaration");
        }

        return paren;
    }


    //<block> → <statement> | <statement> <block>
    private Node block(Node paren, int begin, int end) throws Exception {

        //skip empty lines before and after
        while (tokens_.get(begin).kindName().equals("NextLine")){
            lineCount++;
            begin++;

        }
        while (tokens_.get(end).kindName().equals("NextLine")||tokens_.get(end).kindName().equals("Empty")){
            end--;
        }
        end++;//keep the last "NextLine"



        //take the line into a statement
        tempNode = new Node("<statement>");
        tempNode = statement(tempNode,begin);
        paren.addKid(tempNode);
        lineCount++;
        //call recursion on block while pos_ not reach the end of <block>
        if (pos_<end) {
            paren = block(paren, pos_, end);
        }
        return paren;
    }



    //<statement> → <if_statement> | <while_statement> | <print_statement> | <assignment_statement>
    private Node statement(Node paren, int begin) throws Exception {
        //skip empty lines before and after
        while (tokens_.get(begin).kindName().equals("NextLine")){
            lineCount++;
            begin++;
        }

        //if begin with and a String_Literal (<id>)
        if (tokens_.get(begin).kindName().equals("String_Literal")){
            //check "="
            if (tokens_.get(begin+1).kind_==Token.Kinds.Assign){
                tempNode = new Node("<assign_statement>");
                tempNode = assign_statement(tempNode,begin);
                paren.addKid(tempNode);
            }
            else {
                System.out.println("Line " + lineCount + " ERROR: Invalid statement");
                pos_=length;
                throw new Exception("Line " + lineCount + " ERROR: Invalid statement");
            }
        }
        else if (tokens_.get(begin).kind_== Token.Kinds.Keyword_If||
                tokens_.get(begin).kind_== Token.Kinds.Keyword_While||
                tokens_.get(begin).kind_== Token.Kinds.Keyword_Repeat||
                tokens_.get(begin).kind_== Token.Kinds.Keyword_Print)
        {
            switch (tokens_.get(begin).kindName()) {
                case "Keyword_If": {
                    //find where <if_statement> ends
                    for (int i = begin; i < length; i++) {
                        if (tokens_.get(i).kind_ == Token.Kinds.Keyword_End) {
                            tempNode = new Node("<if_statement>");
                            tempNode = if_statement(tempNode,begin,i-1);
                            paren.addKid(tempNode);
                            pos_ = i + 1;
                            break;
                        }
                    }
                    break;
                }
                case "Keyword_While": {
                    //find where <while_statement> ends
                    for (int i = begin; i < length; i++) {
                        if (tokens_.get(i).kind_ == Token.Kinds.Keyword_End) {
                            tempNode = new Node("<while_statement>");
                            tempNode = while_statement(tempNode,begin,i-1);
                            paren.addKid(tempNode);
                            pos_ = i + 1;
                            break;
                        }
                    }
                    break;
                }
                case "Keyword_Repeat":{
                    //find where <repeat_statement> ends
                    for (int i = begin; i < length; i++) {

                        if (tokens_.get(i).kind_ == Token.Kinds.Keyword_End) {

                            tempNode = new Node("<repeat_statement>");
                            tempNode = repeat_statement(tempNode,begin,i-1);
                            paren.addKid(tempNode);
                            pos_ = i + 1;
                            break;
                        }
                    }
                    break;
                }
                case "Keyword_Print": {
                    //check if follow by "("
                    if (tokens_.get(begin+1).kind_ == Token.Kinds.LeftParenthesis) {
                        //check end with ")"
                        for (int i = begin; i < length; i++) {
                            if (tokens_.get(i).kind_ == Token.Kinds.RightParenthesis) {
                                pos_ = i + 1;
                                tempNode = new Node("<print_statement>");
                                tempNode = print_statement(tempNode, begin, i);
                                paren.addKid(tempNode);
                                break;
                            }
                            if (i==length-1) {
                                System.out.println("Line " + lineCount + " ERROR: Invalid print_statement: Missing RightParenthesis");
                                throw new Exception("Line " + lineCount + " ERROR: Invalid print_statement: Missing RightParenthesis");
                            }
                        }
                    }
                    break;
                }
                default:{
                    System.out.println("Line " + lineCount + " ERROR: Invalid statement: unidentified keyword");
                    pos_=length;
                    throw new Exception("Line " + lineCount + " ERROR: Invalid statement: unidentified keyword");
                }
            }
        }
        else {
            System.out.println("Line " + lineCount + " ERROR: Invalid statement: can't begin with "+tokens_.get(begin).kindName());
            pos_=length;
            throw new Exception("Line " + lineCount + " ERROR: Invalid statement: can't begin with "+tokens_.get(begin).kindName());
        }
        return paren;
    }


    //<if_statement> → if <boolean_expression> then <block> else <block> end
    private Node if_statement(Node paren, int begin, int end) throws Exception {
        int range=end-begin;

        if (range<4){
            System.out.println("Line " + lineCount + " ERROR: Invalid if_statement: invalid size");
            throw new Exception("Line " + lineCount + " ERROR: Invalid if_statement: invalid size");
        }

        paren.addKid(new Node("if"));

        //get the range of boolean expression
        int boolBegin=begin+1;
        int boolEnd=0;
        for (int i =begin+1;i<end;i++){
            if (tokens_.get(i).kind_== Token.Kinds.Keyword_Then){
                boolEnd=i-1;
                break;
            }
            if (i==end-1){
                System.out.println("Line " + lineCount + " ERROR: Invalid if_statement: Missing Keyword_Then");
                throw new Exception("Line " + lineCount + " ERROR: Invalid if_statement: Missing Keyword_Then");
            }
        }

        tempNode = new Node("<boolean_expression>");
        tempNode = boolean_expression(tempNode,boolBegin,boolEnd);
        paren.addKid(tempNode);


        paren.addKid(new Node("then"));


        //get the range of first <block> and determent if second <block> is needed
        int firstBlockBegin = boolEnd+2;
        int firstBlockEnd = 0;
        boolean hasElse = false;
        for (int i =begin+1;i<end;i++){
            if (tokens_.get(i).kind_== Token.Kinds.Keyword_Else){
                firstBlockEnd=i-1;
                hasElse = true;
                break;
            }
            if (i==end-1){
                hasElse =false;
                firstBlockEnd = end-1;
            }
        }

        //if 1st <block> is empty return empty block
        if (firstBlockBegin==end) {
            paren.addKid(new Node("<block>"));
            return paren;
        }
        else if (firstBlockBegin<=firstBlockEnd || firstBlockBegin>end-1){
            tempNode = new Node("<block>");
            tempNode = block(tempNode,firstBlockBegin,firstBlockEnd);
            paren.addKid(tempNode);
        }
        else {
            System.out.println("Line " + lineCount + " ERROR: Invalid if_statement: first <block> size invalid");
            throw new Exception("Line " + lineCount + " ERROR: Invalid if_statement: first <block> size invalid");
        }


        int secondBlockBegin=0;
        int secondBlockEnd=0;
        if (hasElse){
            secondBlockBegin = firstBlockEnd+2;
            secondBlockEnd = end-1;

            paren.addKid(new Node("else"));

        }
        //if 2nd <block> is empty return empty block
        if (secondBlockBegin==end){
            paren.addKid(new Node("<block>"));
            return paren;
        }
        else if (secondBlockBegin<=secondBlockEnd || secondBlockBegin>end-1){
            tempNode = new Node("<block>");
            tempNode = block(tempNode,secondBlockBegin,secondBlockEnd);
            paren.addKid(tempNode);
        }
        else {
            System.out.println("Line " + lineCount + " ERROR: Invalid if_statement: first <block> size invalid");
            throw new Exception("Line " + lineCount + " ERROR: Invalid if_statement: first <block> size invalid");
        }

        paren.addKid(new Node("end"));
        return paren;
    }








    //<while_statement> → while <boolean_expression> do <block> end
    private Node while_statement(Node paren, int begin, int end) throws Exception {
        int range=end-begin;

        if (range<3){
            System.out.println("Line " + lineCount + " ERROR: Invalid while_statement: invalid size");
            throw new Exception("Line " + lineCount + " ERROR: Invalid while_statement: invalid size");
        }

        paren.addKid(new Node("while"));

        //get the range of boolean expression
        int boolBegin=begin+1;
        int boolEnd=0;
        for (int i =begin+1;i<end;i++){
            if (tokens_.get(i).kind_== Token.Kinds.Keyword_Do){
                boolEnd=i-1;
                break;
            }
            if (i==end-1){
                System.out.println("Line " + lineCount + " ERROR: Invalid while_statement: Missing Keyword_Do");
                throw new Exception("Line " + lineCount + " ERROR: Invalid while_statement: Missing Keyword_Do");
            }
        }

        tempNode = new Node("<boolean_expression>");
        tempNode = boolean_expression(tempNode,boolBegin,boolEnd);
        paren.addKid(tempNode);

        paren.addKid(new Node("do"));

        //get the range of <block>
        int blockBegin = boolEnd+2;
        int blockEnd = end-1;
        //if <block> is empty return empty block
        if (blockBegin>=end) {
            paren.addKid(new Node("<block>"));
            return paren;
        }
        else if (blockBegin<=blockEnd || blockBegin>end-1){
            tempNode = new Node("<block>");
            tempNode = block(tempNode,blockBegin,blockEnd);
            paren.addKid(tempNode);
        }
        else {
            System.out.println("Line " + lineCount + " ERROR: Invalid while_statement: <block> size invalid");
            throw new Exception("Line " + lineCount + " ERROR: Invalid while_statement: <block> size invalid");
        }

        paren.addKid(new Node("end"));
        return paren;
    }





    //<repeat_statement> → repeat <block> until <boolean_expression> end
    private Node repeat_statement(Node paren, int begin, int end) throws Exception {
        int range=end-begin;

        if (range<3){
            System.out.println("Line " + lineCount + " ERROR: Invalid repeat_statement: invalid size");
            throw new Exception("Line " + lineCount + " ERROR: Invalid repeat_statement: invalid size");
        }

        paren.addKid(new Node("repeat"));

        //get the range of <block>
        int blockBegin=begin+1;
        int blockEnd=0;
        for (int i =begin+1;i<end;i++){
            if (tokens_.get(i).kind_== Token.Kinds.Keyword_Until){
                blockEnd=i-1;
                break;
            }
            if (i==end-1){
                System.out.println("Line " + lineCount + " ERROR: Invalid repeat_statement: Missing Keyword_Until");
                throw new Exception("Line " + lineCount + " ERROR: Invalid repeat_statement: Missing Keyword_Until");
            }
        }

        //return empty <block> if begin greater than end
        if (blockBegin>blockEnd) {
            paren.addKid(new Node("<block>"));
        }
        else {
            tempNode = new Node("<block>");
            tempNode = block(tempNode, blockBegin, blockEnd);
            paren.addKid(tempNode);
        }


        paren.addKid(new Node("until"));

        //get the range of <boolean_expression>
        int boolBegin = blockEnd+2;
        int boolEnd = end-1;
        //if <boolean expression> is empty return empty block
        if (boolBegin>=end) {
            System.out.println("Line " + lineCount + " ERROR: Invalid repeat_statement: Invalid boolean_expression size");
            throw new Exception("Line " + lineCount + " ERROR: Invalid repeat_statement: Invalid boolean_expression size");
        }
        else if (blockBegin<=blockEnd || blockBegin<end-1){
            tempNode = new Node("<boolean_expression>");
            tempNode = boolean_expression(tempNode,boolBegin,boolEnd);
            paren.addKid(tempNode);

        }
        else {
            System.out.println("Line " + lineCount + " ERROR: Invalid while_statement: <block> size invalid");
            throw new Exception("Line " + lineCount + " ERROR: Invalid while_statement: <block> size invalid");
        }

        paren.addKid(new Node("end"));
        return paren;
    }




    //<print_statement> → print ( <arithmetic_expression> )
    private Node print_statement(Node paren, int begin, int end) throws Exception {
        paren.addKid(new Node("print"));
        paren.addKid(new Node("("));

        tempNode = new Node("<arithmetic_expression>");
        tempNode = arithmetic_expression(tempNode,begin+2,end-1);
        paren.addKid(tempNode);

        paren.addKid(new Node(")"));
        return paren;
    }


    //<assign_statement> -> id <assign> <arithmetic_expression>
    private Node assign_statement(Node paren, int begin) throws Exception {
        //find where the <assign_statement> end
        int end=0;
        for (int i =begin;i<length;i++){
            //end at 1 token before NextLine
            if (tokens_.get(i).kind_==Token.Kinds.NextLine){
                end=i-1;
                //advanced pos_ for next statement
                pos_=i+1;
                break;
            }
        }

        //check if right side of "=" is not empty
        if (end>begin+1){
            //create tempNode and link the the String_Literal to an Id
            tempNode =new Node("<id>");
            tempNode.addKid(new Node(tokens_.get(begin).createID()));

            //store id in identifier table if not existed in identifier table
            if (!id.containsKey(tokens_.get(begin).value())){
                id.put(tokens_.get(begin).value(),null);
            }
            //add id to the parent node
            paren.addKid(tempNode);

            //add "="
            paren.addKid(new Node(tokens_.get(begin+1)));



            //add <arithmetic_expression> to paren
            tempNode = new Node("<arithmetic_expression>");
            tempNode = arithmetic_expression(tempNode,begin+2,end);
            paren.addKid(tempNode);
        }
        else{
            System.out.println("Line "+lineCount+" ERROR: Invalid assign_statement");
            throw new Exception("Line "+lineCount+" ERROR: Invalid assign_statement");
        }
        return paren;
    }


    //<arithmetic_expression> → <id> | <Integer> | <arithmetic_op> <arithmetic_expression> <arithmetic_expression>
    private Node arithmetic_expression(Node paren, int begin, int end) throws Exception {
        //if only one token in the expression
        if (begin==end){
            //if "id"
            if (tokens_.get(begin).kind_== Token.Kinds.String_Literal){
                //check if id exist in identifier table
                if (id.containsKey(tokens_.get(begin).value())){
                    //create tempNode and transform the String_Literal to an Id
                    tempNode = new Node("<id>");
                    tempNode.addKid(new Node(tokens_.get(begin).createID()));
                    //check the id exist in the identifier table
                    if (!id.containsKey(tokens_.get(begin).value())){
                        System.out.println("Line "+lineCount+" ERROR: ID doesn't exist");
                        throw new Exception("Line "+lineCount+" ERROR: ID doesn't exist");
                    }
                    //add id to the parent node
                    paren.addKid(tempNode);
                }
                else {
                    System.out.println("Line "+lineCount+" ERROR: wrong ID kind");
                    throw new Exception("Line "+lineCount+" ERROR: wrong ID kind");
                }
            }
            // if "<Integer>"
            else if (tokens_.get(begin).kind_== Token.Kinds.Integer){
                tempNode = new Node("<Integer>");
                tempNode.addKid(new Node(tokens_.get(begin).createID()));
                paren.addKid(tempNode);
            }
            else {
                System.out.println("Line "+lineCount+" ERROR: Invalid ID OR int");
                throw new Exception("Line "+lineCount+" ERROR: Invalid ID OR int");
            }
        }
        //else if at least 3 token then it's "<arithmetic_op> <arithmetic_expression> <arithmetic_expression>"
        else if (begin+2<=end){
            //<arithmetic_op>
            tempNode = new Node("<arithmetic_op>");
            tempNode = arithmetic_op(tempNode,begin);
            paren.addKid(tempNode);

            //if operator follow by another operator
            if (tokens_.get(begin+1).kind_== Token.Kinds.Plus||
                tokens_.get(begin+1).kind_== Token.Kinds.Minus||
                tokens_.get(begin+1).kind_== Token.Kinds.Asterisk||
                tokens_.get(begin+1).kind_== Token.Kinds.Slash)
            {
                //first <arithmetic_expression>
                tempNode = new Node("<arithmetic_expression>");
                tempNode = arithmetic_expression(tempNode,begin+1,end-1);
                paren.addKid(tempNode);
                //second <arithmetic_expression>
                tempNode = new Node("<arithmetic_expression>");
                tempNode = arithmetic_expression(tempNode,end,end);
                paren.addKid(tempNode);
            }
            else if (begin+2==end){
                //first <arithmetic_expression>
                tempNode = new Node("<arithmetic_expression>");
                tempNode = arithmetic_expression(tempNode,begin+1,begin+1);
                paren.addKid(tempNode);
                //second <arithmetic_expression>
                tempNode = new Node("<arithmetic_expression>");
                tempNode = arithmetic_expression(tempNode,end,end);
                paren.addKid(tempNode);
            }
            else {
                System.out.println("Line "+lineCount+" ERROR: Invalid arithmetic_expression");
                throw new Exception("Line "+lineCount+" ERROR: Invalid arithmetic_expression");
            }
        }
        else {
            System.out.println("Line "+lineCount+" ERROR: Invalid arithmetic_expression: right of '=' can't be empty");
            throw new Exception("Line "+lineCount+" ERROR: Invalid arithmetic_expression: right of '=' can't be empty");
        }

        return paren;
    }


    //<arithmetic_op> → add_operator | sub_operator | mul_operator | div_operator
    private Node arithmetic_op(Node paren, int begin) throws Exception {
        switch (tokens_.get(begin).kindName()){
            case "Plus"     :
            case "Minus"    :
            case "Asterisk" :
            case "Slash"    :
                paren.addKid(new Node(tokens_.get(begin)));break;
            default:
                System.out.println("Line "+lineCount+" ERROR: Invalid arithmetic_operator");
                throw new Exception("Line "+lineCount+" ERROR: Invalid arithmetic_operator");
        }
        return paren;
    }



    //<boolean_expression> -> <relate_op> <arithmetic_expression> <arithmetic_expression>
    private Node boolean_expression(Node paren, int begin, int end) throws Exception {
        //check size
        if (end-begin!=2){
            System.out.println("Line "+lineCount+" ERROR: Invalid boolean_expression: Invalid size");
            throw new Exception("Line "+lineCount+" ERROR: Invalid boolean_expression: Invalid size");
        }

        tempNode = new Node("<relate_op>");
        tempNode = relate_op(tempNode,begin);
        paren.addKid(tempNode);


        tempNode = new Node("<arithmetic_expression>");
        tempNode = arithmetic_expression(tempNode,begin+1,begin+1);
        paren.addKid(tempNode);

        tempNode = new Node("<arithmetic_expression>");
        tempNode =  arithmetic_expression(tempNode,begin+2,begin+2);
        paren.addKid(tempNode);


        return paren;
    }


    //<relative_op> → le_operator | lt_operator | ge_operator | gt_operator | eq_operator | ne_operator
    private Node relate_op(Node paren, int begin) throws Exception {
        switch (tokens_.get(begin).kindName()){
            case "Less"         :
            case "Greater"      :
            case "LessEqual"    :
            case "GreaterEqual" :
            case "Equal"        :
            case "NotEqual"     :
                paren.addKid(new Node(tokens_.get(begin)));break;
            default:
                System.out.println("Line "+lineCount+" ERROR: Invalid relation_operator");
                throw new Exception("Line "+lineCount+" ERROR: Invalid relation_operator");
        }

        return paren;
    }

}
