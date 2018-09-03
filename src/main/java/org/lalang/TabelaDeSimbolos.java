package org.lalang;

import java.util.Hashtable;

class TabelaDeSimbolos {
    private Hashtable<String, EntradaSimbolo> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new Hashtable<String, EntradaSimbolo>();
    }

    public boolean adicionarEntrada(String nome, String tipo, String tipoDeDado) {
        if(this.tabela.get(nome) == null) {
            this.tabela.put(nome, new EntradaSimbolo(nome, tipo, tipoDeDado));

            return true;
        }

        return false;
    }

    public EntradaSimbolo encontrarEntrada(String nome) {
        return this.tabela.get(nome);
    }
}