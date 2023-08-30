/*
 * Class:       CS 4308 Section 02
 * Term:        Spring 2022
 * Name:        Kainuo He
 * Instructor:  Sharon Perry
 * Project:     Julia Programming Language Interpreter
 */

import java.io.BufferedReader;
import java.util.Map;

public class Interpreter {

    //create variables
    LexicalAnalyzer lex;
    SyntaxAnalyzer syn;
    Node root;
    Map<String,Token> id;


    //constructor
    public Interpreter(BufferedReader file) throws Exception {
        //create LexicalAnalyzer and analyze
        lex = LexicalAnalyzer.create(file);
        lex.analyze();
        //create SyntaxAnalyzer and analyze
        syn = new SyntaxAnalyzer(lex);
        syn.analyze();

        //populate variables
        root = syn.root;
        this.id = syn.id;
    }

    //method to print tokens and parse tree
    public void printTokens() {
        lex.tokens_.stream().filter( e -> e.kind() != Token.Kinds.Separator ).forEach(System.out::println);
    }
    public void printParseTree(){
        root.print();
    }
    public void printParseTreeWithoutValue(){
        root.printNoValue();
    }


    //major method to interpret
    public void run() throws Exception {
        //pass the tree root to analyze
        analyze(root);
    }

    //method to analyze a Node and will analyze it's kids recursively
    private String analyze(Node paren) throws Exception {
        switch (paren.nodeName){
            // return the value for id, int
            case "<id>": case "<Integer>":
                return paren.getKid(0).getVale();


            //check relate_op and arithmetic_op is valid
            case "<relate_op>":
                switch (paren.getKid(0).getVale()){
                    case "<": case "<=": case ">": case ">=": case "==": case "~=":
                        return paren.getKid(0).getVale();
                    default:
                        System.out.println("RUN TIME ERROR: Invalid relate_op: "+paren.getKid(0).getVale());
                        throw new Exception("RUN TIME ERROR: Invalid relate_op: "+paren.getKid(0).getVale());
                }
            case "<arithmetic_op>":
                switch (paren.getKid(0).getVale()){
                    case "+": case "-": case "*": case "/":
                        return paren.getKid(0).getVale();
                    default:
                        System.out.println("RUN TIME ERROR: Invalid arithmetic_op: "+paren.getKid(0).getVale());
                        throw new Exception("RUN TIME ERROR: Invalid arithmetic_op: "+paren.getKid(0).getVale());
                }


            case "<program>":
                analyze(paren.getKid(4));
                break;

            case "<block>":
                for (int i =0; i < paren.kidNodes.size();i++){
                    analyze(paren.getKid(i));
                }
                break;

            case "<statement>":
                analyze(paren.getKid(0));
                break;

            case "<if_statement>":
                if (analyze(paren.getKid(1)).equals("1")){ //if <if_statement> is TRUE
                    analyze(paren.getKid(3));
                }
                else if (paren.kidNodes.size()==7) {  //<if_statement> is FALSE, has ELSE
                    analyze(paren.getKid(5));
                }
                else if (paren.kidNodes.size()==5){ //<if_statement> is FALSE, no ELSE
                    //do nothing
                }
                else {
                    System.out.println("RUN TIME ERROR: Invalid if_statement size");
                    throw new Exception("RUN TIME ERROR: Invalid if_statement size");
                }
                break;

            case "<while_statement>":
                while (analyze(paren.getKid(1)).equals("1")){
                    analyze(paren.getKid(3));
                }
                break;

            case "<repeat_statement>":
                do {
                    analyze(paren.getKid(1));
                }while (analyze(paren.getKid(3)).equals("1"));
                break;

            case "<assign_statement>":
                //assign value in the identifier table
                id.replace(analyze(paren.getKid(0)), arithmeticExpression(paren.getKid(2)));
                break;

            case "<print_statement>":
                System.out.println(arithmeticExpression(paren.getKid(2)).value());
                break;

            case "<boolean_expression>":
                Token temp1 = arithmeticExpression(paren.getKid(1));    //get token of the first <arithmetic_expression>
                Token temp2 = arithmeticExpression(paren.getKid(2));    //get token of the first <arithmetic_expression>
                int i1 = Integer.parseInt(temp1.value());               //convert temp1 value to int
                int i2 = Integer.parseInt(temp2.value());               //convert temp2 value to int
                switch (analyze(paren.getKid(0))){
                    case "<=":
                        if (i1<=i2)return "1";
                        else return "0";
                    case "<":
                        if (i1<i2)return "1";
                        else return "0";
                    case ">=":
                        if (i1>=i2)return "1";
                        else return "0";
                    case ">":
                        if (i1>i2)return "1";
                        else return "0";
                    case "==":
                        if (i1==i2)return "1";
                        else return "0";
                    case "~=":
                        if (i1!=i2)return "1";
                        else return "0";
                    default:
                        System.out.println("RUN TIME ERROR: Invalid relate_op: "+analyze(paren.getKid(0)));
                        throw new Exception("RUN TIME ERROR: Invalid relate_op: "+analyze(paren.getKid(0)));
                }

            default:
                System.out.println("RUN TIME ERROR: Invalid Node \""+paren.nodeName+"\"");
                throw new Exception("RUN TIME ERROR: Invalid Node \""+paren.nodeName+"\"");
        }


        //return "1" (true) on default
        return "1";

    }



    //independent method for arthmeticExpression since it need to return a Token instead of String
    private Token arithmeticExpression(Node paren) throws Exception {
        String temp;
        Token t1;
        Token t2;
        int i1;
        int i2;

        //<id>
        if (paren.kidNodes.size()==1 && paren.getKid(0).getVale()=="<id>"){
            temp = analyze(paren.getKid(0));
            if (id.containsKey(temp)) return id.get(temp);
            else {
                System.out.println("RUN TIME ERROR: No <id> named \""+temp+"\" found in the Identifier table");
                throw new Exception("RUN TIME ERROR: No <id> named \""+temp+"\" found in the Identifier table");
            }
        }
        //<Integer>
        else if (paren.kidNodes.size()==1 && paren.getKid(0).getVale()=="<Integer>"){
            return paren.getKid(0).getKid(0).token;
        }
        //<arithmetic_op> <arithmetic_expression> <arithmetic_expression>
        else {
            t1 = arithmeticExpression(paren.getKid(1)); //first <arithmetic_expression> token
            t2 = arithmeticExpression(paren.getKid(2)); //second <arithmetic_expression> token
            i1 = Integer.parseInt(t1.value());          //first <arithmetic_expression> value in int
            i2 = Integer.parseInt(t2.value());          //second <arithmetic_expression> value in int
            switch (analyze(paren.getKid(0))){
                case "+":
                    temp = String.valueOf(i1+i2);
                    return new Token(temp,Token.Kinds.Integer);
                case "-":
                    temp = String.valueOf(i1-i2);
                    return new Token(temp,Token.Kinds.Integer);
                case "*":
                    temp = String.valueOf(i1*i2);
                    return new Token(temp,Token.Kinds.Integer);
                case "/":
                    temp = String.valueOf(i1/i2);
                    return new Token(temp,Token.Kinds.Integer);
                default:
                    System.out.println("RUN TIME ERROR: Invalid <arithmetic_op>, can NOT be \""+analyze(paren.getKid(0))+"\"");
                    throw new Exception("RUN TIME ERROR: Invalid <arithmetic_op>, can NOT be \""+analyze(paren.getKid(0))+"\"");
            }
        }
    }


}
