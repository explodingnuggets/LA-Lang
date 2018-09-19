package org.lalang;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

class PilhaDeTabelas {
    private static PilhaDeTabelas instancia;
    private Stack<TabelaDeSimbolos> pilha;
    private Hashtable<String, EntradaFuncao> funcoes;
    private Hashtable<String, EntradaTipo> tipos;

    private PilhaDeTabelas() {
        this.pilha = new Stack<TabelaDeSimbolos>();
        this.pilha.add(new TabelaDeSimbolos(""));
        this.funcoes = new Hashtable<String, EntradaFuncao>();
        this.tipos = new Hashtable<String, EntradaTipo>();
    }

    /*
    * Aqui está sendo utilizado o padrão Singleton, para que só exista uma
    * única pilha de tabelas de símbolos durante toda a execução do programa.
    */
    public static PilhaDeTabelas getInstancia() {
        if(PilhaDeTabelas.instancia == null)
            PilhaDeTabelas.instancia = new PilhaDeTabelas();

        return PilhaDeTabelas.instancia;
    }

    public TabelaDeSimbolos novaTabela(String tipoRetorno) {
        TabelaDeSimbolos tabela = new TabelaDeSimbolos(tipoRetorno);
        this.pilha.add(tabela);

        return tabela;
    }

    public TabelaDeSimbolos novaTabela() {
        TabelaDeSimbolos tabela = new TabelaDeSimbolos(this.getTabela().getTipoRetorno());
        this.pilha.add(tabela);

        return tabela;
    }

    public boolean adicionarSimbolo(String nome, String tipo, String tipoDeDado) {
        if(this.encontrarSimbolo(nome) == null) {
            return this.getTabela().adicionarEntrada(nome, tipo, tipoDeDado);
        } else {
            return false;
        }
    }

    public EntradaSimbolo encontrarSimbolo(String nome) {
        EntradaSimbolo simbolo;

        for(TabelaDeSimbolos tabela: this.pilha) {
            simbolo = tabela.encontrarEntrada(nome);
            if(simbolo != null)
                return simbolo;
        }

        return null;
    }

    public EntradaSimbolo encontrarVariavel(String nome) {
        EntradaSimbolo simbolo = this.encontrarSimbolo(nome);

        if(simbolo != null && simbolo.getTipo().equals("variavel")) {
            return simbolo;
        }

        return null;
    }

    public TabelaDeSimbolos getTabela() {
        return this.pilha.peek();
    }

    public void removerTabela() {
        this.pilha.pop();
    }

    public boolean adicionarFuncao(String nome, String tipoRetorno) {
        if(this.adicionarSimbolo(nome, "funcao", tipoRetorno)) {
            this.funcoes.put(nome, new EntradaFuncao(nome, tipoRetorno));

            return true;
        }
    
        return false;
    }

    public boolean adicionarFuncao(String nome, String tipoRetorno, List<EntradaSimbolo> parametros) {
        if(this.adicionarSimbolo(nome, "funcao", tipoRetorno)) {
            this.funcoes.put(nome, new EntradaFuncao(nome, tipoRetorno, parametros));

            return true;
        }

        return false;
    }

    public EntradaFuncao encontrarFuncao(String nome) {
        return this.funcoes.get(nome);
    }

    public boolean adicionarTipo(String nome, List<EntradaSimbolo> campos) {
        if(this.adicionarSimbolo(nome, "tipo", "")) {
            this.tipos.put(nome, new EntradaTipo(nome, campos));

            return true;
        }

        return false;
    }

    public EntradaTipo encontrarTipo(String nome) {
        return this.tipos.get(nome);
    }

    public static void reset() {
        PilhaDeTabelas.instancia = null;
    }
}