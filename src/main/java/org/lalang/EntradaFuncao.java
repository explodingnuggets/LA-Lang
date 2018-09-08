package org.lalang;

import java.util.ArrayList;
import java.util.List;

class EntradaFuncao {
    private String nome;
    private String tipoRetorno;
    private List<EntradaSimbolo> parametros;

    public EntradaFuncao(String nome, String tipoRetorno) {
        this.nome = nome;
        this.tipoRetorno = tipoRetorno;
        this.parametros = new ArrayList<EntradaSimbolo>();
    }

    public EntradaFuncao(String nome, String tipoRetorno, List<EntradaSimbolo> parametros) {
        this.nome = nome;
        this.tipoRetorno = tipoRetorno;
        this.parametros = parametros;
    }

    public String getNome() {
        return this.nome;
    }

    public String getTipoRetorno() {
        return this.tipoRetorno;
    }

    public List<EntradaSimbolo> getParametros() {
        return this.parametros;
    }
}