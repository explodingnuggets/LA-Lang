grammar LA;

@header {
    package org.lalang;
}

// REGRAS SINTÁTICAS

programa
    : EOF;

// REGRAS LÉXICAS

IDENT
    : [a-zA-Z_][a-zA-Z_0-9]*;

CADEIA
    : '"' ~('\\'|'"')* '"';

NUM_INT
    : DIGIT+;

NUM_FLOAT
    : DIGIT+ ',' DIGIT+;

fragment DIGIT
    : [0-9]+;

programa: EOF;
