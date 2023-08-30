/*
 * Class:       CS 4308 Section 02
 * Term:        Spring 2022
 * Name:        Kainuo He
 * Instructor:  Sharon Perry
 * Project:     Julia Programming Language Interpreter
 */

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
