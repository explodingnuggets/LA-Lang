package org.lalang;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

// Em LABaseVisitor temos visits para todas as 
// regras da gramática, em LASemantico o que se  
// faz é aplicar a semântica da linguagem, ou 
// seja, o contexto nas regras, como manter o  
// controle dos escopos de funções e, então
// verificar e imprimir os erros de compilação
// relativos à semântica 
class LASemantico extends LABaseVisitor<String> {
    private ErrorBuffer out;
    private PilhaDeTabelas pilha;

    // recebe o arquivo em que será impressa a saída
    // e gera a pilha de tabelas que fará as separações
    // de escopos
    public LASemantico(ErrorBuffer out) {
        this.out = out;
        this.pilha = PilhaDeTabelas.getInstancia();
    }

    // verifica se o tipo existe, ou se já foi
    // declarado um novo tipo com aquele nome
    // (na pilha de tabelas).
    // Caso o tipo comece com '^'' se olha o 
    // resto da string já que o '^' só simboliza
    // se tratar de um ponteiro 
    public boolean existeTipo(String tipo) {
        if(tipo.charAt(0) == '^'){
            tipo = tipo.substring(1);
        }

        if(tipo.equals("literal") || tipo.equals("inteiro") || tipo.equals("real") || tipo.equals("logico") || this.pilha.encontrarTipo(tipo) != null) {
            return true;
        }

        return false;
    }

    // Declaração de registros, com suas variáveis
    // adicionando-se os devidos caminhos para acessá-las
    // com o '.' 
    public void declareRegistro(LAParser.RegistroContext ctx, String prefix) {
        for(LAParser.VariavelContext varCtx: ctx.variavel()) {
            for(LAParser.IdentificadorContext identCtx: varCtx.identificador()) {
                // adiciona-se o '.' no caminho do registrador para seu
                // identificador (o qual pode ser um registrador também,
                // chamando-se recursivamente)
                String nome = prefix + "." + this.identificadorName(identCtx);
                // caso o identificador não seja um registro,
                // não precisa haver recursão
                if(varCtx.tipo().registro() == null) {
                    // verifica se já existe na tabela de símbolos
                    if(!this.pilha.adicionarSimbolo(nome, "variavel", varCtx.tipo().getText()))
                    this.out.println("Linha " + identCtx.start.getLine() + ": identificador " + nome + " ja declarado anteriormente");
                
                //caso for um registro 
                } else {
                    // verifica se já existe na tabela de símbolos
                    if(this.pilha.encontrarVariavel(nome) != null) {
                        this.out.println("Linha " + identCtx.start.getLine() + ": identificador " + nome + " ja declarado anteriormente");
                    
                    // caso não exista, declarar esse
                    // novo registro recursivamente
                    } else {    
                        this.declareRegistro(varCtx.tipo().registro(), nome);
                    }
                }
            }
        }
    }

    // Declaração de um tipo de registro
    // declarando-se os tipos de identificadores
    // que terão dentro dele
    public void tipoRegistro(LAParser.RegistroContext ctx, String prefix, List<EntradaSimbolo> campos) {
        for(LAParser.VariavelContext varCtx: ctx.variavel()) {
            for(LAParser.IdentificadorContext identCtx: varCtx.identificador()) {
                String nome = prefix + this.identificadorName(identCtx);

                if(varCtx.tipo().registro() == null) {
                    campos.add(new EntradaSimbolo(nome, "variavel", varCtx.tipo().getText()));
                } else {
                    this.tipoRegistro(varCtx.tipo().registro(), nome + ".", campos);
                }
            }
        }
    }

    // Modifica-se o visit de declarações
    // de constantes adicionando-se elas
    // na pilha
    @Override
    public String visitDeclConstante(LAParser.DeclConstanteContext ctx) {
        String nome = ctx.IDENT().getText();
        String tipo = ctx.tipo_basico().getText();

        this.pilha.adicionarSimbolo(nome, "variavel", tipo);

        return null;
    }

    // Modifica-se o visit de declaraçoes
    // de tipo adicionando-os na pilha caso
    // não sejam registros ou chamando
    // tipoRegistro quando forem
    @Override
    public String visitDeclTipo(LAParser.DeclTipoContext ctx) {
        String nome = ctx.IDENT().getText();
        List<EntradaSimbolo> campos = new ArrayList<EntradaSimbolo>();

        if(ctx.tipo().registro() == null) {
            campos.add(new EntradaSimbolo("", "variavel", ctx.tipo().getText()));
        } else {
            this.tipoRegistro(ctx.tipo().registro(), "", campos);
        }

        this.pilha.adicionarTipo(nome, campos);

        return null;
    }

    // Modifica-se o visit do comando
    // 'Se' adicionando-se uma nova tabela
    // na pilha (novo escopo) e uma nova tabela 
    // para o Senao (caso necessário)
    @Override
    public String visitCmdSe(LAParser.CmdSeContext ctx) {
        /*
        * São necessários dois escopos diferentes para o comando se, um para os
        * códigos onde o caso é verdadeiro, e outro para o caso de senão.
        */
        // Começo do escopo de se
        visitExpressao(ctx.expressao());

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

    // Verifa-se se a variável na qual
    // se está armazendo o valor lido foi
    // declarando olhando-se na pilha de 
    // tabelas
    @Override
    public String visitCmdLeia(LAParser.CmdLeiaContext ctx){
        String nome_variavel = this.identificadorName(ctx.first);

        if( pilha.encontrarVariavel(nome_variavel) == null ){
            this.out.println("Linha " + ctx.start.getLine() + ": identificador " + nome_variavel + " nao declarado");
        }

        for(LAParser.IdentificadorContext identCtx: ctx.rest) {
            nome_variavel = this.identificadorName(identCtx);

            if(this.pilha.encontrarVariavel(nome_variavel) == null)
                this.out.println("Linha " + identCtx.start.getLine() + ": identificador " + nome_variavel + " nao declarado");
        }

        visitChildren(ctx);

        return null;
    }

    // Cria-se novos escopos para os
    // casos e para o senão
    @Override
    public String visitCmdCaso(LAParser.CmdCasoContext ctx) {
        visitExp_aritmetica(ctx.exp_aritmetica());

        // novo escopo para os casos
        this.pilha.novaTabela();

        visitSelecao(ctx.selecao());

        this.pilha.removerTabela();
        // novo escopo para o senão
        this.pilha.novaTabela();

        for(LAParser.CmdContext cmdCtx: ctx.cmd()) {
            visitCmd(cmdCtx);
        }

        this.pilha.removerTabela();

        return null;
    }

    // Cria-se novo escopo para o 'para'
    @Override
    public String visitCmdPara(LAParser.CmdParaContext ctx) {
        this.pilha.novaTabela();

        //this.pilha.adicionarSimbolo(ctx.IDENT().getText(), "inteiro");

        visitChildren(ctx);

        this.pilha.removerTabela();

        return null;
    }

    // Cria-se o novo escopo para o 'enquanto'
    @Override
    public String visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        this.pilha.novaTabela();

        visitChildren(ctx);

        this.pilha.removerTabela();

        return null;
    }

    // Cria-se o novo escopo
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
        String nome = this.identificadorName(ctx.identificador());

        EntradaSimbolo simbolo = this.pilha.encontrarVariavel(nome);
        // Caso a variável nao foi encontrada
        // na tabela de símbolos significa que ela
        // não foi declarada
        if(simbolo == null) {
            nome += ctx.identificador().dimensao().getText();
            out.println("Linha " + ctx.identificador().start.getLine() + ": identificador " + nome + " nao declarado");
        } else {
            String tipo = simbolo.getTipoDeDado();
            // ve se a variável é um ponteiro
            if(ctx.ptr != null && tipo.charAt(0) == '^') {
                tipo = tipo.substring(1);
                nome = "^" + nome;

            }

            String tipoExpressao = visitExpressao(ctx.expressao());

            // Se o tipo do valor atribuído não ser o tipo
            // da variável colocar mensagem de erro
            // inteiro pode ser castado pra real
            if(!tipo.equals(tipoExpressao) && !(tipo.equals("real") && tipoExpressao.equals("inteiro"))) {
                nome += ctx.identificador().dimensao().getText();
                
                this.out.println("Linha " + ctx.identificador().start.getLine() + ": atribuicao nao compativel para " + nome);
            }
        }

        return null;
    }

    // verifica se a função na pilha possui
    // o mesmo número de parâmetros passados
    // para esta chamada, e então se os tipos
    // são equivalentes
    @Override
    public String visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        String nome = ctx.IDENT().getText();
        List<EntradaSimbolo> parametros = this.pilha.encontrarFuncao(nome).getParametros();
        List<String> tipos = new ArrayList<String>();

        for(LAParser.ExpressaoContext expCtx: ctx.expressao()) {
            tipos.add(visitExpressao(expCtx));
        }

        if(parametros.size() == tipos.size()) {
            for(int i = 0; i < parametros.size(); i++) {
                if(!parametros.get(i).getTipoDeDado().equals(tipos.get(i))) {
                    System.out.println(parametros.get(i).getTipo() + " " +tipos.get(i));
                    this.out.println("Linha " + ctx.start.getLine() + ": incompatibilidade de parametros na chamada de " + nome);

                    break;
                }
            }
        } else {
            this.out.println("Linha " + ctx.start.getLine() + ": incompatibilidade de parametros na chamada de " + nome);
        }

        return null;
    }

    // verifica se há um tipo de retorno para
    // esse escopo mesmo (essa função)
    @Override
    public String visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        if(this.pilha.getTabela().getTipoRetorno().equals("")) {
            this.out.println("Linha " + ctx.start.getLine() + ": comando retorne nao permitido nesse escopo");
        }

        return null;
    }

    @Override
    public String visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        String nome = ctx.IDENT().getText();
        String tipo;
        if(ctx.type != null) {
            tipo = ctx.type.getText();
        } else {
            tipo = "";
        }

        List<EntradaSimbolo> parametros = new ArrayList<EntradaSimbolo>();
        if(ctx.parametros() != null) {
            for(LAParser.ParametroContext parCtx: ctx.parametros().parametro()) {
                String tipoPar = parCtx.tipo_estendido().getText();

                for(LAParser.IdentificadorContext identCtx: parCtx.identificador()) {
                    String nomePar = this.identificadorName(identCtx);

                    parametros.add(new EntradaSimbolo(nomePar, "variavel", tipoPar));
                }
            }

            this.pilha.adicionarFuncao(nome, tipo, parametros);
        } else {
            this.pilha.adicionarFuncao(nome, tipo);
        }

        this.pilha.novaTabela(tipo);

        for(EntradaSimbolo simbolo: parametros) {
            EntradaTipo tipoEntrada = this.pilha.encontrarTipo(simbolo.getTipoDeDado());

            this.pilha.adicionarSimbolo(simbolo.getNome(), simbolo.getTipo(), simbolo.getTipoDeDado());

            if(tipoEntrada != null) {
                for(EntradaSimbolo campo: tipoEntrada.getCampos()) {
                    this.pilha.adicionarSimbolo(simbolo.getNome() + "." + campo.getNome(), "variavel", campo.getTipoDeDado());
                }
            }
        }

        visitChildren(ctx);

        this.pilha.removerTabela();

        return null;
    }

    @Override
    public String visitDeclVariavel(LAParser.DeclVariavelContext ctx) {
        for(LAParser.IdentificadorContext identCtx: ctx.variavel().identificador()) {
            String nome = this.identificadorName(identCtx);

            if(ctx.variavel().tipo().registro() == null) {
                if(ctx.variavel().tipo().tipo_estendido().tipo_basico_ident().tipo_basico() != null) {
                    if(!this.pilha.adicionarSimbolo(nome, "variavel" ,ctx.variavel().tipo().getText()))
                        this.out.println("Linha " + identCtx.start.getLine() + ": identificador " + nome + " ja declarado anteriormente");
                } else {
                    String tipoNome = ctx.variavel().tipo().tipo_estendido().tipo_basico_ident().IDENT().getText();
                    EntradaTipo tipo = this.pilha.encontrarTipo(tipoNome);
                    
                    if(this.pilha.adicionarSimbolo(nome, "variavel", tipoNome)) {
                        if(tipo != null) {
                            for(EntradaSimbolo campo: tipo.getCampos()) {
                                this.pilha.adicionarSimbolo(nome + "." + campo.getNome(), "variavel", campo.getTipoDeDado());
                            }
                        }
                    } else {
                        this.out.println("Linha " + identCtx.start.getLine() + ": identificador " + nome + " ja declarado anteriormente");
                    }
                }

                if(!this.existeTipo(ctx.variavel().tipo().getText()))
                    this.out.println("Linha " + ctx.variavel().tipo().start.getLine() + ": tipo " + ctx.variavel().tipo().getText() + " nao declarado");
            } else {
                if(this.pilha.encontrarVariavel(nome) != null) {
                    this.out.println("Linha " + identCtx.start.getLine() + ": identificador " + nome + " ja declarado anteriormente");
                } else {
                    this.declareRegistro(ctx.variavel().tipo().registro(), nome);
                }
            }
        }

        return null;
    }

    public String identificadorName(LAParser.IdentificadorContext ctx) {
        String nome = ctx.first.getText();
        //this.pilha.adicionarSimbolo(nome, tipo);

        for(Token ident: ctx.rest) {
            nome += "." + ident.getText();
        }

        return nome;
    }

    @Override
    public String visitParcela_unario(LAParser.Parcela_unarioContext ctx) {
        if(ctx.var != null) {
            String nome = this.identificadorName(ctx.identificador());
            EntradaSimbolo simbolo = this.pilha.encontrarVariavel(nome);

            if(simbolo == null) {
                this.out.println("Linha " + ctx.identificador().start.getLine() + ": identificador " + nome + " nao declarado");
            } else {
                return simbolo.getTipoDeDado();
            }
        } else if(ctx.func != null) {
            String nome = ctx.IDENT().getText();
            EntradaFuncao funcao = this.pilha.encontrarFuncao(nome);
            List<String> tipos = new ArrayList<String>();

            for(LAParser.ExpressaoContext expCtx: ctx.expressao()) {
                tipos.add(visitExpressao(expCtx));
            }

            if(funcao.getParametros().size() == tipos.size()) {
                for(int i = 0; i < tipos.size(); i++) {
                    if(!funcao.getParametros().get(i).getTipoDeDado().equals(tipos.get(i))) {
                        this.out.println("Linha " + ctx.start.getLine() + ": incompatibilidade de parametros na chamada de " + nome);

                        break;
                    }
                }
            } else {
                this.out.println("Linha " + ctx.start.getLine() + ": incompatibilidade de parametros na chamada de " + nome);
            }

            return funcao.getTipoRetorno();
        } else if(ctx.inteiro != null) {
            return "inteiro";
        } else if(ctx.real != null) {
            return "real";
        } else {
            return visitExpressao(ctx.expr);
        }

        return "";
    }

    @Override
    public String visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {
        if(ctx.cadeia != null) {
            return "literal";
        } else {
            String nome = this.identificadorName(ctx.identificador());
            EntradaSimbolo simbolo = this.pilha.encontrarVariavel(nome);

            if(simbolo == null) {
                this.out.println("Linha " + ctx.identificador().start.getLine() + ": identificador " + nome + " nao declarado");
            } else {
                return "^" + simbolo.getTipoDeDado();
            }
        }

        return "";
    }

    @Override
    public String visitParcela(LAParser.ParcelaContext ctx) {
        if(ctx.parcela_unario() != null) {
            return visitParcela_unario(ctx.parcela_unario());
        } else {
            return visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
    }

    @Override
    public String visitFator(LAParser.FatorContext ctx) {
        String tipo = visitParcela(ctx.first);

        for(LAParser.ParcelaContext parcelaCtx: ctx.rest) {
            String novoTipo = visitParcela(parcelaCtx);
            if(!tipo.equals(novoTipo)) {
                if((tipo.equals("inteiro") && novoTipo.equals("real"))
                || (tipo.equals("real") && novoTipo.equals("inteiro"))) {
                    tipo = "real";
                } else {
                    return "";
                }
            }
        }

        return tipo;
    }

    @Override
    public String visitTermo(LAParser.TermoContext ctx) {
        String tipo = visitFator(ctx.first);

        for(LAParser.FatorContext fatorCtx: ctx.rest) {
            String novoTipo = visitFator(fatorCtx);
            if(!tipo.equals(novoTipo)) {
                if((tipo.equals("inteiro") && novoTipo.equals("real"))
                || (tipo.equals("real") && novoTipo.equals("inteiro"))) {
                    tipo = "real";
                } else {
                    return "";
                }
            }
        }

        return tipo;
    }

    @Override
    public String visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        String tipo = visitTermo(ctx.first);

        for(LAParser.TermoContext termoCtx: ctx.rest) {
            String novoTipo = visitTermo(termoCtx);
            if(!tipo.equals(novoTipo)) {
                if((tipo.equals("inteiro") && novoTipo.equals("real"))
                || (tipo.equals("real") && novoTipo.equals("inteiro"))) {
                    tipo = "real";
                } else {
                    return "";
                }
            }
        }

        return tipo;
    }

    @Override
    public String visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        String tipo = visitExp_aritmetica(ctx.first);

        if(ctx.second != null) {
            String novoTipo = visitExp_aritmetica(ctx.second);
            if(!tipo.equals(novoTipo)) {
                if((tipo.equals("inteiro") && novoTipo.equals("real"))
                || (tipo.equals("real") && novoTipo.equals("inteiro"))) {
                    tipo = "real";
                } else {
                    return "";
                }
            }

            return "logico";
        }

        return tipo;
    }

    @Override
    public String visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        if(ctx.logical != null) {
            return "logico";
        } else {
            return visitExp_relacional(ctx.exp_relacional());
        }
    }

    @Override
    public String visitFator_logico(LAParser.Fator_logicoContext ctx) {
        return visitParcela_logica(ctx.parcela_logica());
    }

    @Override
    public String visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        String tipo = visitFator_logico(ctx.first);

        for(LAParser.Fator_logicoContext fatorCtx: ctx.rest) {
            if(!tipo.equals("logico") && !visitFator_logico(fatorCtx).equals("logico")) {
                return "";
            }
        }

        return tipo;
    }

    @Override
    public String visitExpressao(LAParser.ExpressaoContext ctx) {
        String tipo = visitTermo_logico(ctx.first);

        for(LAParser.Termo_logicoContext termoCtx: ctx.rest) {
            if(!tipo.equals("logico") && !visitTermo_logico(termoCtx).equals("logico")) {
                return "";
            }
        }

        return tipo;
    }
}
