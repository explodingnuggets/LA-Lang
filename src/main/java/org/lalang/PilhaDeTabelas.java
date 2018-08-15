package org.lalang;

import java.util.Stack;

class PilhaDeTabelas {
    private PilhaDeTabelas instancia;
    private Stack<TabelaDeSimbolos> pilha;

    private PilhaDeTabelas() {
        this.pilha = new Stack<TabelaDeSimbolos>();
    }

    /*
    * Aqui está sendo utilizado o padrão Singleton, para que só exista uma
    * única pilha de tabelas de símbolos durante toda a execução do programa.
    */
    public PilhaDeTabelas getInstancia() {
        if(this.instancia == null)
            this.instancia = new PilhaDeTabelas();

        return this.instancia;
    }

    public TabelaDeSimbolos novaTabela() {
        TabelaDeSimbolos tabela = new TabelaDeSimbolos();
        this.pilha.add(tabela);

        return tabela;
    }

    public TabelaDeSimbolos getTabela() {
        return this.pilha.peek();
    }

    public void removerTabela() {
        this.pilha.pop();
    }
}