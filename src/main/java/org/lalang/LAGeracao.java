package org.lalang;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

class LAGeracao extends LABaseListener {
    private StringBuffer out;
    private PilhaDeTabelas pilha;

    public LAGeracao(StringBuffer out) {
        this.out = out;
        PilhaDeTabelas.reset();
        this.pilha = PilhaDeTabelas.getInstancia();
    }

    public String parseIdentificador(LAParser.IdentificadorContext ctx) {
        String nome = ctx.first.getText();

        for(Token ident: ctx.rest) {
            nome += ident.getText();
        }

        return nome;
    }

    public String parseTipoEstendido(LAParser.Tipo_estendidoContext ctx) {
        String tipo = this.typeToCFormat(ctx.tipo_basico_ident().getText());

        if(ctx.pointer == null) {
            return tipo;
        } else {
            return tipo + "*";
        }
    }

    public String typeToCFormat(String tipo) {
        switch(tipo) {
            case "inteiro":
                return "int";
            case "real":
                return "double";
            case "literal":
                return "char*";
            case "logico":
                return "int";
            default:
                return tipo;
        }
    }

    public String typeToCPrintf(String tipo) {
        switch(tipo) {
            case "int":
                return "%d";
            case "double":
                return "%f";
            case "char*":
                return "%s";
            default:
                return "";
        }
    }

    public String getTipo(String tipo) {
        if(tipo=="inteiro"){
            return "int";
        }
        else if(tipo=="literal"){
            return "string";
        }
        else if(tipo=="real"){
            return "float";
        }
        else if(tipo=="logico"){
            return "int";
        }
        return null;
    }

    @Override
    public void enterPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("#include <stdio.h>\n");
        this.out.append("#include <stdlib.h>\n");
    }

    @Override
    public void exitPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("return 0;\n}");
    }

    @Override
    public void exitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        this.out.append("int main() {\n");
    }

    @Override
    public void enterDeclVariavel(LAParser.DeclVariavelContext ctx) {
        this.out.append(getTipo(ctx.variavel().tipo().getText())+ " " + ctx.variavel().first.getText()+";\n");
    }

    
    @Override 
    public void enterCmdLeia(LAParser.CmdLeiaContext ctx) {

    }
}
