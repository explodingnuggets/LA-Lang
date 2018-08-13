package org.lalang;

import java.util.Hashtable;

class TabelaDeSimbolos {
    private Hashtable<String, EntradaSimbolo> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new Hashtable<String, EntradaSimbolo>();
    }

    public boolean adicionarEntrada(String nome, TipoDeDado tipo) {
        if(this.tabela.get(nome) == null) {
            this.tabela.put(nome, new EntradaSimbolo(nome, tipo));

            return true;
        }

        return false;
    }

    public boolean adicionarEntrada(String nome, TipoDeDado tipo, String valor) {
        if(this.adicionarEntrada(nome, tipo)) {
            this.encontrarEntrada(nome).setValor(valor);

            return true;
        }

        return false;
    }

    public EntradaSimbolo encontrarEntrada(String nome) {
        return this.tabela.get(nome);
    }
}