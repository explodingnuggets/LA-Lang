package org.lalang;

import java.util.Hashtable;

class TabelaDeSimbolos {
    private Hashtable<String, EntradaSimbolo> tabela;

    public TabelaDeSimbolos() {
        this.tabela = new Hashtable<String, EntradaSimbolo>();
    }

    public boolean adicionarEntrada(String nome, String tipo, int linha) {
        if(this.tabela.get(nome) == null) {
            this.tabela.put(nome, new EntradaSimbolo(nome, tipo));

            return true;
        }

        ErrorListener.out.println("Linha " + linha + ": identificador " + nome + "ja declarado anteriormente");
        return false;
    }

    public boolean adicionarEntrada(String nome, String tipo, String valor, int linha) {
        if( (this.tabela.get(nome) != null) && this.tabela.get(nome).getTipo()==tipo) {
            this.encontrarEntrada(nome).setValor(valor);

            return true;
        }

        return false;
    }

    public EntradaSimbolo encontrarEntrada(String nome) {
        return this.tabela.get(nome);
    }
}