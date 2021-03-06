package org.lalang;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

class LAMain {
    public static void main(String[] args){
        //se recebeu o número de argumentos errado mostra como eles devem ser inseridos
        if(args.length < 1) {
            System.out.println("java -jar la source_code [output_file]");
        } else {
            try {
                /*
                * Instanciação básica dos elementos do compilador.
                * Primeiramente o arquivo com o código fonte é aberto, e seu 
                * conteúdo é passado para o lexer. Os tokens gerados são passados
                * pro parser, e em seguida a AST é caminhada pelo visitor LASemantico,
                * onde a análise semântica e a análise de código será feita.
                */
                StringBuffer out = new StringBuffer();
                ErrorBuffer error_out = new ErrorBuffer();

                ErrorListener error_listener = new ErrorListener(error_out);

                CharStream input = CharStreams.fromFileName(args[0]);
                LALexer lexer = new LALexer(input);
                //lexer.removeErrorListeners();
                lexer.addErrorListener(error_listener);

                CommonTokenStream tokens = new CommonTokenStream(lexer);
                LAParser parser = new LAParser(tokens);
                //parser.removeErrorListeners();
                parser.addErrorListener(error_listener);
                LAParser.ProgramaContext ast = parser.programa();
                
                //caso não houveram erros sintáticos verificar se houveram erros semânticos
                if(!error_out.modified()) {
                    LASemantico semantico = new LASemantico(error_out);
                    semantico.visitPrograma(ast);
                }

                //caso não ouveram erros semânticos também gerar o código em C
                if(!error_out.modified()) {
                    LAGeracao geracao = new LAGeracao(out);
                    geracao.visitPrograma(ast);
                }

                // Se não foram passados dois argumentos, a saída será escrita no stdout (terminal)
                // Se foram passados dois argumentos, o segundo será tratado como
                // o caminho de um arquivo, onde a saída será escrita
                if(args.length < 2) {
                    if(!error_out.modified()) {
                        System.out.println(out);
                    } else {
                        System.out.println(error_out);
                    }
                } else {
                    BufferedWriter file_out = new BufferedWriter(new FileWriter(new File(args[1])));

                    if(!error_out.modified()) {
                        file_out.write(out.toString());
                    } else {
                        file_out.write(error_out.toString());
                        file_out.write("Fim da compilacao\n");
                    }

                    file_out.flush();
                    file_out.close();
                }
            //caso haja uma excessão ao lidar com o arquivo imprimir mensagem correspondente
            } catch (IOException exception) {
                System.out.println("Erro ao abrir o arquivo: " + exception.getMessage());
            }
        }
    }
}
