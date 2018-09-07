package org.lalang;

import java.util.Collection;
import java.util.Hashtable;

class TabelaDeSimbolos {
    private Hashtable<String, EntradaSimbolo> tabela;
    private String tipoRetorno;

    public TabelaDeSimbolos(String tipoRetorno) {
        this.tabela = new Hashtable<String, EntradaSimbolo>();
        this.tipoRetorno = tipoRetorno;
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

    public String getTipoRetorno() {
        return this.tipoRetorno;
    }

    public Collection<EntradaSimbolo> getSimbolos() {
        return this.tabela.values();
    }
}