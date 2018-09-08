# LA-Lang
Um compilador para a linguagem LA, com código resultante em C.

## Especificações

A linguagem LA, foi criada para facilitar o ensino de algortimos e programação, permitindo trabalhar com uma linguagem simples, porém com os elementos essenciais de uma linguagem. Grande parte da sua sintaxe pode ser facilmente compilada para a linguagem C, sendo esse o objetivo desse compilador.

A seguir, são apresentados alguns elementos dessa linguagem:

### Sintaxe Básica

```
declare
    a, b: inteiro

algoritmo
    leia(a, b)

    se a > b entao
        escreva("a+b=", a+b)
    senao
        escreva("a-b=", a-b)
    fim_se
fim_algoritmo
```

Na linguagem LA, as declarações são feitas no início do código, no começo da declaração da função ou procedimento ou no começo do algoritmo. Além disso, as operações de entrada e saída são feitas pelos comandos leia e escreva, respectivamente.

### Registros

```
registro
    nome: literal
    idade, ra: inteiro
    ira: real
    cursando: logico
fim_registro
```

Registros definem estruturas para o agrupamento de dados relevantes, da mesma maneira que `structs` na linguagem C. Esses registros podem ser usados diretamente, ou podem ser associados a um tipo, como será mostrado em seguida.

### Tipos

```
tipo aluno:
    registro
        nome: literal
        idade, ra: inteiro
        ira: real
        cursando: logico
    fim_registro
```

Assim como `typedef` em C, podemos associar um tipo pré-existente, ou um registro à um identificador personalizado. No caso acima, associamos o registro criado à um tipo aluno.

### Ponteiros

```
declare
    x: inteiro
declare
    y: ^inteiro

algoritmo
    x <- 1
    y <- &x
    ^y <- 2
    escreva("x = ", x)
fim_algoritmo
```

Os ponteiros na linguagem LA, funcionam da mesma maneira que ponteiros na linguagem C, onde se tivermos uma variável y, com um endereço apontando para uma variável do tipo inteiro, podemos atribuir um valor para a mesma, utilizando o operador `^`. Já se quisermos acessar o endereço de uma variável x, podemos utilizar o operador `&`.

## Estruturas e Técnicas Utilizadas

### Tabela de Símbolos

### Pilha de Tabelas de Símbolos

