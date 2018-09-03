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

        //this.pilha.adicionarSimbolo(ctx.IDENT().getText(), "inteiro");

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
        for(LAParser.IdentificadorContext identCtx: ctx.variavel().identificador()) {
            String nome = this.identificadorName(identCtx);

            if(ctx.variavel().tipo().registro() == null) {
                //this.pilha.adicionarSimbolo(nome, ctx.variavel().tipo().getText());
            } else {
                this.declRegistro(ctx.variavel().tipo().registro(), nome);
            }
        }

        return null;
    }

    public String identificadorName(LAParser.IdentificadorContext ctx) {
        String nome = ctx.first.getText();

        for(Token ident: ctx.rest) {
            nome += "." + ident.getText();
        }

        return nome;
    }

    public void declRegistro(LAParser.RegistroContext ctx, String prefix) {
        for(LAParser.VariavelContext varCtx: ctx.variavel()) {
            for(LAParser.IdentificadorContext identCtx: varCtx.identificador()) {
                String nome = this.identificadorName(identCtx);

                System.out.println(prefix + "." + nome + ": " + varCtx.tipo().getText());
                if(varCtx.tipo().registro() == null) {
                    //this.pilha.adicionarSimbolo(prefix + "." + nome, varCtx.tipo().getText());
                } else {
                    this.declRegistro(varCtx.tipo().registro(), prefix + "." + nome);
                }
            }
        }
    }

    @Override
    public String visitDeclTipo(LAParser.DeclTipoContext ctx) {
        // TODO: adicionar tipos no escopo

       return null;
    }

    @Override
    public String visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if(ctx.var != null) {
            String nome = this.identificadorName(ctx.identificador());

            System.out.println(nome);
        } else if(ctx.func != null) {

        } else if(ctx.inteiro != null) {

        } else if(ctx.real != null) {

        } else {

        }

        return null;
    }
}
