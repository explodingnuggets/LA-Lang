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

    @Override
    public void enterPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("#include <stdio.h>\n");
        this.out.append("#include <stdlib.h>\n\n");
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
        if(ctx.variavel().tipo().registro() == null) {
            String variavelTipo = this.parseTipoEstendido(ctx.variavel().tipo().tipo_estendido());

            for(LAParser.IdentificadorContext identCtx: ctx.variavel().identificador()) {
                String variavelNome = this.parseIdentificador(identCtx);
                
                this.pilha.adicionarSimbolo(variavelNome, "variavel", variavelTipo);
                this.out.append(variavelTipo + " " + variavelNome + ";\n");
            }
        }
    }

    @Override
    public void enterCmdLeia(LAParser.CmdLeiaContext ctx) {
        this.out.append("scanf(\"");
        List<String> variaveis = new ArrayList<String>();
        
        String nome = this.parseIdentificador(ctx.first);
        String tipo = this.pilha.encontrarVariavel(nome).getTipoDeDado();
        
        variaveis.add(nome);
        this.out.append(this.typeToCPrintf(tipo));

        this.out.append("\"");
        for(String variavel: variaveis) {
            this.out.append(",&" + variavel);
        }

        this.out.append(");\n");
    }

    @Override
    public void enterCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        this.out.append("printf(\"");
        List<String> expressoes = new ArrayList<String>();

        String nome=this.parseIdentificador(ctx.first.first.first.parcela_logica().exp_relacional().first.first.first.first.parcela_unario().var);
        String tipo = this.pilha.encontrarVariavel(nome).getTipoDeDado();

        expressoes.add(nome);
        this.out.append(this.typeToCPrintf(tipo));

        this.out.append("\"");
        for(String expressao:expressoes) {
            this.out.append("," + expressao);
        }

        this.out.append(");\n");
    }
}