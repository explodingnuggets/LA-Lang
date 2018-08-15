package org.lalang;

class LASemantico extends LABaseVisitor<Object> {
    private StringBuffer out;

    public LASemantico(StringBuffer out) {
        this.out = out;
    }

    @Override
    public Object visitDecl_procedimento(LAParser.Decl_procedimentoContext ctx) {
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
}