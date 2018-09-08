package org.lalang;

class LAGeracao extends LABaseListener {
    private StringBuffer out;

    public LAGeracao(StringBuffer out) {
        this.out = out;
    }

    @Override
    public void enterPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("#include <stdio.h>\n");
        this.out.append("#include <stdlib.h>\n");
    }

    @Override
    public void exitPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("}");
    }

    @Override
    public void exitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        this.out.append("int main() {\n");
    }
}