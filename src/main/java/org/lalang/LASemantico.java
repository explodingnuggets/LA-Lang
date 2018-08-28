package org.lalang;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

class LASemantico extends LABaseVisitor<String> {
    private ErrorBuffer out;
    private PilhaDeTabelas pilha;

    public LASemantico(ErrorBuffer out) {
        this.out = out;
        this.pilha = PilhaDeTabelas.getInstancia();
    }

    public void checarTipo(String tipo, String valor) {
        
    }

    @Override
    public String visitPrograma(LAParser.ProgramaContext ctx) {
        visitDeclaracoes(ctx.declaracoes());

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
        /*
        * São necessários dois escopos diferentes para o comando se, um para os
        * códigos onde o caso é verdadeiro, e outro para o caso de senão.
        */
        // Começo do escopo de se
        this.pilha.novaTabela();

        for(LAParser.CmdContext cmdCtx: ctx.seCmd) {
            visitCmd(cmdCtx);
        }

        this.pilha.removerTabela();
        // Fim do escopo de se
        // Começo do escopo de senão
        this.pilha.novaTabela();

        for(LAParser.CmdContext cmdCtx: ctx.senaoCmd) {
            visitCmd(cmdCtx);
        }

        this.pilha.removerTabela();
        // Fim do escopo de se

        return null;
    }

    @Override
    public String visitCmdCaso(LAParser.CmdCasoContext ctx) {
        visitExp_aritmetica(ctx.exp_aritmetica());

        this.pilha.novaTabela();

        visitSelecao(ctx.selecao());

        this.pilha.removerTabela();
        this.pilha.novaTabela();

        for(LAParser.CmdContext cmdCtx: ctx.cmd()) {
            visitCmd(cmdCtx);
        }

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitCmdPara(LAParser.CmdParaContext ctx) {
        this.pilha.novaTabela();

        this.pilha.adicionarSimbolo(ctx.IDENT().getText(), "inteiro", ctx.start.getLine());

        visitChildren(ctx);

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        this.pilha.novaTabela();

        visitChildren(ctx);

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitCmdFaca(LAParser.CmdFacaContext ctx) {
        this.pilha.novaTabela();

        for(LAParser.CmdContext cmdCtx: ctx.cmd()) {
            visitCmd(cmdCtx);
        }

        this.pilha.removerTabela();

        visitExpressao(ctx.expressao());

        return null;
    }

    @Override
    public String visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String nome = ctx.identificador().first.getText();

        for(Token identCtx: ctx.identificador().rest) {
            nome += "." + identCtx.getText();
        }

        EntradaSimbolo simbolo = this.pilha.encontrarVariavel(nome);
        if(simbolo == null) {
            out.println("Linha " + ctx.identificador().start.getLine() + ": identificador nao declarado");
        } else {
            String tipo = simbolo.getTipo();

            this.checarTipo(tipo, ctx.expressao().getText());
        }

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

            //this.pilha.adicionarSimbolo(nome, tipo);
        }

        return null;
    }

    @Override
    public String visitDeclTipo(LAParser.DeclTipoContext ctx) {
        // TODO: adicionar tipos no escopo

        return null;
    }
}
