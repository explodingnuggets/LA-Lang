package org.lalang;

import java.util.HashMap;
import java.util.Stack;

class PilhaDeTabelas {
    private static PilhaDeTabelas instancia;
    private Stack<TabelaDeSimbolos> pilha;

    private PilhaDeTabelas() {
        this.pilha = new Stack<TabelaDeSimbolos>();
        this.pilha.add(new TabelaDeSimbolos());
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

    public TabelaDeSimbolos novaTabela() {
        TabelaDeSimbolos tabela = new TabelaDeSimbolos();
        this.pilha.add(tabela);

        return tabela;
    }

    public boolean adicionarSimbolo(String nome, String tipo, String tipoDeDado) {
        return this.getTabela().adicionarEntrada(nome, tipo, tipoDeDado);
    }

    public EntradaSimbolo encontrarVariavel(String nome) {
        EntradaSimbolo simbolo;

        for(TabelaDeSimbolos tabela: this.pilha) {
            simbolo = tabela.encontrarEntrada(nome);
            if(simbolo != null && simbolo.getTipo().equals("variavel"))
                return simbolo;
        }

        return null;
    }

    public EntradaSimbolo encontrarTipo(String nome) {
        EntradaSimbolo simbolo;

        for(TabelaDeSimbolos tabela: this.pilha) {
            simbolo = tabela.encontrarEntrada(nome);
            if(simbolo != null && simbolo.getTipo().equals("tipo"))
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
}