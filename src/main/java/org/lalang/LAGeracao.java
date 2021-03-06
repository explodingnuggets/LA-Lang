package org.lalang;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

class LAGeracao extends LABaseVisitor<String> {
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
            nome += "." + ident.getText();
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

    public void parseRegistro(LAParser.RegistroContext ctx) {
        this.out.append("struct {\n");
        for(LAParser.VariavelContext varCtx: ctx.variavel()) {
            if(varCtx.tipo().registro() == null) {
                String variavelTipo = this.parseTipoEstendido(varCtx.tipo().tipo_estendido());

                for(LAParser.IdentificadorContext identCtx: varCtx.identificador()) {
                    String variavelNome = this.parseIdentificador(identCtx);

                    if(variavelTipo.equals("char")) {
                        this.out.append(variavelTipo + " " + variavelNome + "[80];\n");
                    } else {
                        this.out.append(variavelTipo + " " + variavelNome + identCtx.dimensao().getText() + ";\n");
                    }
                }
            }
        }
        this.out.append("}");
    }

    public void stackRegistro(LAParser.RegistroContext ctx, String prefix) {
        for(LAParser.VariavelContext varCtx: ctx.variavel()) {
            if(varCtx.tipo().registro() == null) {
                String variavelTipo = this.parseTipoEstendido(varCtx.tipo().tipo_estendido());

                for(LAParser.IdentificadorContext identCtx: varCtx.identificador()) {
                    String variavelNome = this.parseIdentificador(identCtx);

                    this.pilha.adicionarSimbolo(prefix + "." + variavelNome, "variavel", variavelTipo);
                }
            }
        }
    }

    public String typeToCFormat(String tipo) {
        switch(tipo) {
            case "inteiro":
                return "int";
            case "^inteiro":
                return "int*";
            case "real":
                return "float";
            case "^real":
                return "float*";
            case "literal":
                return "char";
            case "^literal":
                return "char*";
            case "logico":
                return "int";
            case "^logico":
                return "int*";
            default:
                return tipo;
        }
    }

    public String typeToCPrintf(String tipo) {
        switch(tipo) {
            case "int":
                return "%d";
            case "float":
                return "%f";
            case "char":
                return "%s";
            case "char*":
                return "%s";
            default:
                return "";
        }
    }

    public String exprToCExpr(String expr) {
        return expr.replaceAll("(?<![<>])=", "==").replaceAll("nao", "!");
    }

    public void parseParametros(LAParser.ParametrosContext ctx) {
        for(LAParser.ParametroContext parCtx: ctx.parametro()) {
            String tipo = this.typeToCFormat(parCtx.tipo_estendido().getText());

            for(LAParser.IdentificadorContext identCtx: parCtx.identificador()) {
                String nome = this.parseIdentificador(identCtx);

                this.pilha.adicionarSimbolo(nome, "variavel", tipo);

                nome = (tipo.equals("char"))?nome+"[80]":nome;
                this.out.append(tipo + " " + nome);
            }
        }
    }

    public String tipoParcelaUnario(LAParser.Parcela_unarioContext ctx) {
        if(ctx.var != null) {
            return this.pilha.encontrarVariavel(this.parseIdentificador(ctx.var)).getTipoDeDado();
        } else if(ctx.func != null) {
            return this.pilha.encontrarFuncao(ctx.func.getText()).getTipoRetorno();
        } else if(ctx.inteiro != null) {
            return "int";
        } else if(ctx.real != null) {
            return "float";
        } else {
            return this.tipoExpressao(ctx.expr);
        }
    }

    public String tipoParcelaNaoUnario(LAParser.Parcela_nao_unarioContext ctx) {
        if(ctx.cadeia != null) {
            return "char*";
        } else {
            String nome = this.parseIdentificador(ctx.identificador());
            return "&" + this.pilha.encontrarVariavel(nome).getTipoDeDado();
        }
    }

    public String tipoParcela(LAParser.ParcelaContext ctx) {
        if(ctx.parcela_unario() != null) {
            return this.tipoParcelaUnario(ctx.parcela_unario());
        } else {
            return this.tipoParcelaNaoUnario(ctx.parcela_nao_unario());
        }
    }

    public String tipoFator(LAParser.FatorContext ctx) {
        String tipo = this.tipoParcela(ctx.first);

        return tipo;
    }

    public String tipoTermo(LAParser.TermoContext ctx) {
        String tipo = this.tipoFator(ctx.first);

        return tipo;
    }

    public String tipoExpAritmetica(LAParser.Exp_aritmeticaContext ctx) {
        String tipo = this.tipoTermo(ctx.first);

        return tipo;
    }

    public String tipoExpRelacional(LAParser.Exp_relacionalContext ctx) {
        String tipo = this.tipoExpAritmetica(ctx.first);

        return tipo;
    }

    public String tipoParcelaLogica(LAParser.Parcela_logicaContext ctx) {
        if(ctx.logical != null) {
            return "int";
        } else {
            return this.tipoExpRelacional(ctx.exp_relacional());
        }
    }

    public String tipoFatorLogico(LAParser.Fator_logicoContext ctx) {
        return this.tipoParcelaLogica(ctx.parcela_logica());
    }

    public String tipoTermoLogico(LAParser.Termo_logicoContext ctx) {
        return this.tipoFatorLogico(ctx.first);
    }

    public String tipoExpressao(LAParser.ExpressaoContext ctx) {
        return this.tipoTermoLogico(ctx.first);
    }

    @Override
    public String visitPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("#include <stdio.h>\n");
        this.out.append("#include <stdlib.h>\n");
        this.out.append("#include <string.h>\n\n");

        this.visitDeclaracoes(ctx.declaracoes());

        this.out.append("int main() {\n");

        this.visitCorpo(ctx.corpo());

        this.out.append("return 0;\n}");

        return null;
    }

    @Override
    public String visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        String nome = ctx.IDENT().getText();
        String tipo = (ctx.type == null)?"void":this.typeToCFormat(ctx.type.getText());

        this.pilha.adicionarFuncao(nome, tipo);

        this.pilha.novaTabela();

        this.out.append(tipo + " " + nome + "(");

        this.parseParametros(ctx.parametros());

        this.out.append(") {\n");

        this.visitChildren(ctx);

        this.out.append("}\n");

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitDeclVariavel(LAParser.DeclVariavelContext ctx) {
        if(ctx.variavel().tipo().registro() == null) {
            String variavelTipo = this.parseTipoEstendido(ctx.variavel().tipo().tipo_estendido());

            for(LAParser.IdentificadorContext identCtx: ctx.variavel().identificador()) {
                String variavelNome = this.parseIdentificador(identCtx);
                
                this.pilha.adicionarSimbolo(variavelNome, "variavel", variavelTipo);
                if(variavelTipo.equals("char")) {
                    this.out.append(variavelTipo + " " + variavelNome + "[80];\n");
                } else {
                    this.out.append(variavelTipo + " " + variavelNome + identCtx.dimensao().getText() + ";\n");
                    
                    EntradaTipo tipo = this.pilha.encontrarTipo(variavelTipo);
                    if(tipo != null) {
                        for(EntradaSimbolo symbol: tipo.getCampos()) {
                            this.pilha.adicionarSimbolo(variavelNome + "." + symbol.getNome(), "variavel", symbol.getTipoDeDado());
                        }
                    }
                }
            }
        } else {
            for(LAParser.IdentificadorContext identCtx: ctx.variavel().identificador()) {
                String variavelNome = this.parseIdentificador(identCtx);

                this.parseRegistro(ctx.variavel().tipo().registro());
                this.stackRegistro(ctx.variavel().tipo().registro(), variavelNome);

                this.out.append(" " + variavelNome + ";\n");
            }
        }

        return null;
    }

    @Override
    public String visitDeclConstante(LAParser.DeclConstanteContext ctx) {
        this.out.append("#define " + ctx.IDENT().getText() + " " + ctx.valor_constante().getText() + "\n");

        return null;
    }

    @Override
    public String visitDeclTipo(LAParser.DeclTipoContext ctx) {
        this.out.append("typedef ");

        if(ctx.tipo().registro() != null) {
            this.parseRegistro(ctx.tipo().registro());
            List<EntradaSimbolo> campos = new ArrayList<EntradaSimbolo>();

            for(LAParser.VariavelContext varCtx: ctx.tipo().registro().variavel()) {
                String variavelTipo = this.parseTipoEstendido(varCtx.tipo().tipo_estendido());

                for(LAParser.IdentificadorContext identCtx: varCtx.identificador()) {
                    String variavelNome = this.parseIdentificador(identCtx);

                    campos.add(new EntradaSimbolo(variavelNome, "variavel", variavelTipo));
                }
            }

            this.pilha.adicionarTipo(ctx.IDENT().getText(), campos);
        }

        this.out.append(" " + ctx.IDENT().getText() + ";\n");

        return null;
    }

    @Override
    public String visitCmdLeia(LAParser.CmdLeiaContext ctx) {
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
        
        return null;
    }

    @Override
    public String visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        this.out.append("printf(\"");
        List<String> expressoes = new ArrayList<String>();

        for(LAParser.ExpressaoContext exprCtx: ctx.expressao()) {
            expressoes.add(exprCtx.getText());
            String tipo = this.tipoExpressao(exprCtx);

            this.out.append(this.typeToCPrintf(tipo));
        }

        this.out.append("\"");
        for(String expressao:expressoes) {
            this.out.append("," + expressao);
        }

        this.out.append(");\n");

        return null;
    }

    @Override
    public String visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String nome = ctx.identificador().getText();
        String tipo = this.pilha.encontrarVariavel(this.parseIdentificador(ctx.identificador())).getTipoDeDado();
        nome = (ctx.ptr == null)?nome:"*"+nome;

        if(tipo.equals("char")) {
            this.out.append("strcpy(" + nome + "," + this.exprToCExpr(ctx.expressao().getText()) + ");\n");
        } else {
            this.out.append(nome + "=" + this.exprToCExpr(ctx.expressao().getText()) + ";\n");
        }

        return null;
    }

    @Override
    public String visitCmdSe(LAParser.CmdSeContext ctx) {
        this.out.append("if(" + this.exprToCExpr(ctx.expressao().getText()) + ") {\n");

        for(LAParser.CmdContext cmdCtx: ctx.seCmd) {
            this.visitCmd(cmdCtx);
        }

        if(ctx.senaoCmd != null) {
            this.out.append("} else {\n");

            for(LAParser.CmdContext cmdCtx: ctx.senaoCmd) {
                this.visitCmd(cmdCtx);
            }
        }

        this.out.append("}\n");

        return null;
    }

    @Override
    public String visitCmdPara(LAParser.CmdParaContext ctx) {
        String nome = ctx.IDENT().getText();

        this.out.append("for(int " + nome + "=" + ctx.from.getText() + ";" + nome + "<=" + ctx.to.getText() + ";" + nome + "++) {\n");

        for(LAParser.CmdContext cmdCtx: ctx.cmd()) {
            this.visitCmd(cmdCtx);
        }

        this.out.append("}\n");

        return null;
    }

    @Override
    public String visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        this.out.append("while(" + this.exprToCExpr(ctx.expressao().getText()) + ") {\n");

        for(LAParser.CmdContext cmdCtx: ctx.cmd()) {
            this.visitCmd(cmdCtx);
        }

        this.out.append("}\n");

        return null;
    }

    @Override
    public String visitCmdFaca(LAParser.CmdFacaContext ctx) {
        this.out.append("do {\n");

        for(LAParser.CmdContext cmdCtx: ctx.cmd()) {
            this.visitCmd(cmdCtx);
        }

        this.out.append("} while(" + this.exprToCExpr(ctx.expressao().getText()) + ");\n");

        return null;
    }

    @Override
    public String visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        this.out.append(ctx.getText() + ";\n");

        return null;
    }

    @Override
    public String visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        this.out.append("return " + this.exprToCExpr(ctx.expressao().getText()) + ";\n");

        return null;
    }

    @Override
    public String visitCmdCaso(LAParser.CmdCasoContext ctx) {
        this.out.append("switch(" + this.exprToCExpr(ctx.exp_aritmetica().getText()) + ") {\n");

        visitSelecao(ctx.selecao());

        if(ctx.senaoCmd != null) {
            this.out.append("default:\n");

            for(LAParser.CmdContext cmdCtx: ctx.senaoCmd) {
                this.visitCmd(cmdCtx);
            }
        }

        this.out.append("}\n");

        return null;
    }

    @Override
    public String visitItem_selecao(LAParser.Item_selecaoContext ctx) {
        visitConstantes(ctx.constantes());

        for(LAParser.CmdContext cmdCtx: ctx.cmd()) {
            this.visitCmd(cmdCtx);
        }

        this.out.append("break;\n");

        return null;
    }

    @Override
    public String visitNumero_intervalo(LAParser.Numero_intervaloContext ctx) {
        int first = Integer.parseInt(ctx.first.getText());
        first = (ctx.first_neg == null)?first:-first;
        int second = first;

        if(ctx.second != null) {
            second = Integer.parseInt(ctx.second.getText());
            second = (ctx.second_neg == null)?second:-second;
        }

        for(int i = first; i <= second; i++) {
            this.out.append("case " + i + ":\n");
        }

        return null;
    }
}
