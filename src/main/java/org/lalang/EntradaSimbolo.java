package org.lalang;

class EntradaSimbolo {
    private String nome;
    private String tipo;
    private String valor;

    public EntradaSimbolo(String nome, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    public EntradaSimbolo(String nome, String tipo, String valor) {
        this.nome = nome;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getNome() {
        return this.nome;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getValor() {
        return this.valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}