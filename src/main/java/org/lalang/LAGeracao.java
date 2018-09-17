package org.lalang;

class LAGeracao extends LABaseListener {
    private StringBuffer out;

    public LAGeracao(StringBuffer out) {
        this.out = out;
    }

    public String getTipo(String tipo) {
        if(tipo=="inteiro"){
            return "int";
        }
        else if(tipo=="literal"){
            return "string";
        }
        else if(tipo=="real"){
            return "float";
        }
        else if(tipo=="logico"){
            return "int";
        }
        return null;
    }

    @Override
    public void enterPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("#include <stdio.h>\n");
        this.out.append("#include <stdlib.h>\n");
    }

    @Override
    public void exitPrograma(LAParser.ProgramaContext ctx) {
        this.out.append("return 0;\n}");
    }

    @Override
    public void exitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        this.out.append("int main() {\n");
    }

    @Override
    public void enterDeclVariavel(LAParser.DeclVariavelContext ctx) {
        this.out.append(getTipo(ctx.variavel().tipo().getText())+ " " + ctx.variavel().first.getText()+";\n");
    }

    
    @Override 
    public void enterCmdLeia(LAParser.CmdLeiaContext ctx) {

    }
}