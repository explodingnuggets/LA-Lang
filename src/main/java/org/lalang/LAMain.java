package org.lalang;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

class LAMain {
    public static void main(String[] args){
        if(args.length < 1) {
            System.out.println("java -jar la source_code");
        } else {
            try {
                CharStream input = CharStreams.fromFileName(args[0]);
                LALexer lexer = new LALexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                LAParser parser = new LAParser(tokens);
                parser.programa();

            } catch (IOException exception) {
                System.out.println("Erro ao abrir o arquivo: " + exception.getMessage());
            }
        }
    }
}