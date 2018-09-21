# LA-Lang
Um compilador para a linguagem LA, com código resultante em C.

## Compilação
Para compilar seu programa em LA para um em C execute no terminala partir da pasta "LA-Lang":
```
java -jar build/libs/LA-Lang.jar programa-em-la > programa-em-c.c
```
Para executar então, compila-se o arquivo C e executa-se o binário gerado:
```
gcc programa-em-c.c -o binario
./binario
```

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
Para poder se armazenar valores e tipos de variáveis utilizou-se a estratégia de armazená-las em uma tabela de símbolos. Sempre que referenciada uma variável ocorre uma busca nessa tabela, o que além de ser usado para a geração de código é usada para a análise semântica. Tal tabela é definida na classe _TabelaDeSimbolos.java_.

### Pilha de Tabelas de Símbolos
Como há o problema de escopos diferentes para as variáveis utilizou-se a estratégia da pilha de tabelas, onde os escopos mais internos vão sendo colocados no topo desta pilha, sempre que ocorrendo uma busca, inserção ou remoção na entrada mais próxima do topo.
