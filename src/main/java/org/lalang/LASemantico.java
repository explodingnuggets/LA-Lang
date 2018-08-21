package org.lalang;

import org.antlr.v4.runtime.tree.TerminalNode;

class LASemantico extends LABaseVisitor<String> {
    private StringBuffer out;
    private PilhaDeTabelas pilha;

    public LASemantico(StringBuffer out) {
        this.out = out;
        this.pilha = PilhaDeTabelas.getInstancia();
    }

    @Override
    public String visitPrograma(LAParser.ProgramaContext ctx) {
        visitDeclaracoes(ctx.declaracoes());

        this.pilha.novaTabela();

        visitCorpo(ctx.corpo());

        return null;
    }

    @Override
    public String visitDeclProcedimento(LAParser.DeclProcedimentoContext ctx) {
        this.pilha.novaTabela();

        visitChildren(ctx);

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitCmdSe(LAParser.CmdSeContext ctx) {
        this.pilha.novaTabela();

        for(LAParser.CmdContext cmdCtx: ctx.seCmd) {
            visitCmd(cmdCtx);
        }

        this.pilha.removerTabela();
        this.pilha.novaTabela();

        for(LAParser.CmdContext cmdCtx: ctx.senaoCmd) {
            visitCmd(cmdCtx);
        }

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitDeclFuncao(LAParser.DeclFuncaoContext ctx) {
        // TODO: inserir função no escopo global

        this.pilha.novaTabela();

        visitChildren(ctx);

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitDeclVariavel(LAParser.DeclVariavelContext ctx) {
        String tipo = ctx.variavel().tipo().getText();

        for(LAParser.IdentificadorContext identCtx: ctx.variavel().identificador()) {
            String nome = "";
            boolean first = true;

            for(TerminalNode ident: identCtx.IDENT()) {;
                if(!first)
                    nome += ".";

                nome += ident.getText();

                first = false;
            }

            this.pilha.adicionarSimbolo(nome, tipo);
        }

        return null;
    }

    @Override
    public String visitDeclTipo(LAParser.DeclTipoContext ctx) {
        // TODO: adicionar tipos no escopo

        return null;
    }
}
