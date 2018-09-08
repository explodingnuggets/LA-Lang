package org.lalang;

import java.util.Collection;
import java.util.List;

class EntradaTipo {
    private String nome;
    private TabelaDeSimbolos campos;

    public EntradaTipo(String nome) {
        this.nome = nome;
        this.campos = new TabelaDeSimbolos("");
    }

    public EntradaTipo(String nome, List<EntradaSimbolo> campos) {
        this.nome = nome;
        this.campos = new TabelaDeSimbolos("");

        for(EntradaSimbolo campo: campos) {
            this.adicionarCampo(campo.getNome(), campo.getTipoDeDado());
        }
    }

    public boolean adicionarCampo(String nome, String tipo) {
        return this.campos.adicionarEntrada(nome, "variavel", tipo);
    }

    public Collection<EntradaSimbolo> getCampos() {
        return this.campos.getSimbolos();
    }

    public String getNome() {
        return this.nome;
    }
}