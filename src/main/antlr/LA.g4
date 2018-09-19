grammar LA;

@header {
    package org.lalang;
}

// REGRAS SINTÁTICAS

programa
    : declaracoes 'algoritmo' corpo 'fim_algoritmo';

declaracoes
    : (decl_local_global)*
    ;

decl_local_global
    : declaracao_local  
    | declaracao_global
    ;

declaracao_local
    : 'declare' variavel                                    # declVariavel
    | 'constante' IDENT ':' tipo_basico '=' valor_constante # declConstante
    | 'tipo' IDENT ':' tipo                                 # declTipo
    ;

variavel
    : first=identificador (',' rest+=identificador)* ':' tipo;

identificador
    : first=IDENT ('.' rest+=IDENT)* dimensao;

dimensao
    : ('[' exp_aritmetica ']')*;

tipo
    : registro | tipo_estendido;

tipo_basico
    : 'literal' | 'inteiro' | 'real' | 'logico';

tipo_basico_ident
    : tipo_basico | IDENT;

tipo_estendido
    : (pointer='^')? tipo_basico_ident;

valor_constante
    : CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso';

registro
    : 'registro' (variavel)* 'fim_registro';

declaracao_global
    : 'procedimento' IDENT '(' (parametros)? ')' (declaracao_local)* (cmd)* 'fim_procedimento'
    | 'funcao' IDENT '(' (parametros)? ')' ':' type=tipo_estendido (declaracao_local)* (cmd)* 'fim_funcao'
    ;

parametro
    : ('var')? identificador (',' identificador)* ':' tipo_estendido;

parametros
    : parametro (',' parametro)*;

corpo
    : (declaracao_local)* (cmd)*;

cmd
    : cmdLeia
    | cmdEscreva
    | cmdSe
    | cmdCaso
    | cmdPara
    | cmdEnquanto
    | cmdFaca
    | cmdAtribuicao
    | cmdChamada
    | cmdRetorne
    ;

cmdLeia
    : 'leia' '(' (pointerFirst='^')? first=identificador (',' (pointerRest+='^')? rest+=identificador)* ')';

cmdEscreva
    : 'escreva' '(' first=expressao (',' rest+=expressao)* ')';

cmdSe
    : 'se' expressao 'entao' (seCmd+=cmd)* ('senao' (senaoCmd+=cmd)*)? 'fim_se';

cmdCaso
    : 'caso' exp_aritmetica 'seja' selecao ('senao' (senaoCmd+=cmd)*)? 'fim_caso';

cmdPara
    : 'para' IDENT '<-' from=exp_aritmetica 'ate' to=exp_aritmetica 'faca' (cmd)* 'fim_para';

cmdEnquanto
    : 'enquanto' expressao 'faca' (cmd)* 'fim_enquanto';

cmdFaca
    : 'faca' (cmd)* 'ate' expressao;

cmdAtribuicao
    : (ptr='^')? identificador '<-' expressao;

cmdChamada
    : IDENT '(' expressao (',' expressao)* ')';

cmdRetorne
    : 'retorne' expressao;

selecao
    : (item_selecao)*;

item_selecao
    : constantes ':' (cmd)*;

constantes
    : numero_intervalo (',' numero_intervalo)*;

numero_intervalo
    : (first_neg=op_unario)? first=NUM_INT ('..' (second_neg=op_unario)? second=NUM_INT)?;

op_unario
    : '-';

exp_aritmetica
    : first=termo (op1 rest+=termo)*;

termo
    : first=fator (op2 rest+=fator)*;

fator
    : first=parcela (op3 rest+=parcela)*;

op1
    : '+' | '-';

op2
    : '*' | '/';

op3
    : '%';

parcela
    : (op_unario)? parcela_unario | parcela_nao_unario;

parcela_unario
    : ('^')? var=identificador
    | func=IDENT '(' expressao (',' expressao)* ')'
    | inteiro=NUM_INT
    | real=NUM_REAL
    | '(' expr=expressao ')'
    ;

parcela_nao_unario
    : '&' identificador | cadeia=CADEIA;

exp_relacional
    : first=exp_aritmetica (op_relacional second=exp_aritmetica)?;

op_relacional
    : '=' | '<>' | '>=' | '<=' | '>' | '<';

expressao
    : first=termo_logico (op_logico_1 rest+=termo_logico)*;

termo_logico
    : first=fator_logico (op_logico_2 rest+=fator_logico)*;

fator_logico
    : ('nao')? parcela_logica;

parcela_logica
    : logical=('verdadeiro' | 'falso')
    | exp_relacional;

op_logico_1
    : 'ou';

op_logico_2
    : 'e';

// REGRAS LÉXICAS

IDENT
    : [a-zA-Z_][a-zA-Z_0-9]*;

CADEIA
    : '"' (~('"')|'\\"')* '"';

NUM_INT
    : DIGIT+;

NUM_REAL
    : DIGIT+ '.' DIGIT+;

fragment DIGIT
    : [0-9];

COMENTARIO
    : '{' ~('}')* '}' -> channel(HIDDEN);

WS
    : ([ \n\r\t]+ | EOF) -> channel(HIDDEN);

ERRO
    : .;
