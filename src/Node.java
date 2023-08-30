/*
 * Class:       CS 4308 Section 02
 * Term:        Spring 2022
 * Name:        Kainuo He
 * Instructor:  Sharon Perry
 * Project:     Julia Programming Language Interpreter
 */

import java.util.ArrayList;
import java.util.List;

public class Node {
    String nodeName;
    Token token;
    List<Node> kidNodes = new ArrayList<>();

    //constructors
    public Node(String nodeName){
        this.nodeName =nodeName;
        this.token = new Token(nodeName, Token.Kinds.String_Literal);
    }
    public Node(Token token){      //if constructed with token, use the value as the nodeName
        this.nodeName = token.value();
        this.token = token;
    }
    public Node(String nodeName, Token token){
        this.nodeName =nodeName;
        this.token =token;
    }


    //modification methods
    public Node getKid(int i){
        return kidNodes.get(i);
    }
    public String getVale(){
        return token.value();
    }


    //method to add kids
    public void addKid(Node kid){
        kidNodes.add(kid);
    }

    //print all kids nodes
    public void print(){
        print_sub();
        System.out.println("");
    }
    private void print_sub(){
        //print current level
        System.out.print(nodeName+" :: ");
        if (kidNodes==null)return;
        for (int i=0; i< kidNodes.size();i++){
            System.out.print(kidNodes.get(i).nodeName+" ");
        }
        //print all kids' levels
        for (int i=0; i< kidNodes.size();i++){
            if (kidNodes.get(i).kidNodes.size()>0){
                System.out.println("");
                kidNodes.get(i).print_sub();
            }
        }
    }

    //print all kids nodes without showing values
    public void printNoValue(){
        printNoValue_sub();
        System.out.println("");
    }
    private void printNoValue_sub(){
        //print current level
        System.out.print(nodeName+" :: ");
        if (kidNodes==null)return;
        for (int i=0; i< kidNodes.size();i++){
            System.out.print(kidNodes.get(i).nodeName+" ");
        }
        //print all kids' levels
        for (int i=0; i< kidNodes.size();i++){
            if (kidNodes.get(i).kidNodes.size()>0&&!kidNodes.get(i).nodeName.equals("<id>")&&!kidNodes.get(i).nodeName.equals("<Integer>"))
            {
                System.out.println("");
                kidNodes.get(i).printNoValue_sub();
            }
        }
    }

}
