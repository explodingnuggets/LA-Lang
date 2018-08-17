package org.lalang;

class LASemantico extends LABaseVisitor<String> {
    private StringBuffer out;

    public LASemantico(StringBuffer out) {
        this.out = out;
    }

    private String toCType(String type) {
        switch(type) {
            case "inteiro":
                return "int";
            case "real":
                return "double";
            case "literal":
                return "char[]";
            case "logico":
                return "int";
            default:
                return null;
        }
    }

    @Override
    public String visitDecl_procedimento(LAParser.Decl_procedimentoContext ctx) {
        /*
        * Aqui o código monta o corpo do procedimento (função sem retorno).
        * 1. É criado o cabeçalho da função: void nome(
        * 2. Os paramêtros da função são gerado: par1, par2
        * 3. É gerado o fechamento de parentêses e abertura de chaves: ) {
        * 4. O corpo da função é visitado, gerando seu próprio código: //corpo
        * 5. É gerado o fechamento de chaves: }
        */
        // Passo 1
        this.out.append("void " + ctx.IDENT() + "(");

        // Passo 2
        visitParametros(ctx.parametros());

        // Passo 3
        this.out.append(") {\n");

        // Passo 4
        for(LAParser.Declaracao_localContext visit_ctx: ctx.declaracao_local()) {
            visitDeclaracao_local(visit_ctx);
        }
        
        for(LAParser.CmdContext visit_ctx: ctx.cmd()) {
            visitCmd(visit_ctx);
        }

        // Passo 5
        this.out.append("}\n");

        return null;
    }

    @Override
    public String visitParametros(LAParser.ParametrosContext ctx) {
        boolean first = true;

        for(LAParser.ParametroContext visit_ctx: ctx.parametro()) {
            if(!first) {
                this.out.append(", ");
            }

            visitParametro(visit_ctx);

            first = false;
        }

        return null;
    }

    @Override
    public String visitParametro(LAParser.ParametroContext ctx) {
        // TODO: Adicionar paramêtro ao escopo
        boolean first = true;

        visitTipo_estendido(ctx.tipo_estendido());

        for(LAParser.IdentificadorContext visit_ctx: ctx.identificador()) {
            if(!first) {
                this.out.append(", ");
            }

            this.out.append(visit_ctx.getText());

            first = false;
        }

        return null;
    }

    @Override
    public String visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        // TODO: Ponteiro

        this.out.append(this.toCType(ctx.tipo_basico_ident().getText()) + " ");

        return null;
    }
}
