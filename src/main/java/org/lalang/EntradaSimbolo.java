package org.lalang;

class EntradaSimbolo {
    private String nome;
    private String tipo;
    private String tipoDeDado;

    public EntradaSimbolo(String nome, String tipo, String tipoDeDado) {
        this.nome = nome;
        this.tipo = tipo;
        this.tipoDeDado = tipoDeDado;
    }

    public String getNome() {
        return this.nome;
    }

    public String getTipo() {
        return this.tipo;
    }
}