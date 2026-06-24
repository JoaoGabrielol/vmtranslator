# VMTranslator

Implementacao das Partes 1 e 2 do VMTranslator, da disciplina de Compiladores.

O projeto traduz comandos da linguagem VM do nand2tetris para Assembly Hack,
contemplando comandos de acesso a memoria, operacoes aritmeticas/logicas,
controle de fluxo, funcoes, chamadas, retorno, bootstrap e traducao de
multiplos arquivos `.vm` em um unico `.asm`.

## Integrantes

- Matheus Ryan Carreiro Costa Correia
- Joao Gabriel de Oliveira Lopes - 2020002740

## Linguagem

- Java 17
- Maven

## Estrutura

- `src/main/java/parser`: leitura, limpeza de comentarios e classificacao dos comandos VM
- `src/main/java/codewriter`: geracao do codigo Assembly Hack
- `src/main/java/VMTranslator.java`: ponto de entrada do tradutor
- `src/test/java`: testes unitarios
- `projects/07`: arquivos do Project 07 do nand2tetris usados para validacao
- `projects/08`: arquivos do Project 08 usados para validar controle de fluxo e funcoes

## Escopo Implementado

### Parte 1

- Comandos `push` e `pop`
- Segmentos `constant`, `local`, `argument`, `this`, `that`, `temp`, `pointer` e `static`
- Operacoes `add`, `sub`, `neg`, `eq`, `gt`, `lt`, `and`, `or` e `not`

### Parte 2

- Controle de fluxo: `label`, `goto` e `if-goto`
- Declaracao de funcoes com `function`
- Chamada de funcoes com `call`
- Retorno de funcoes com `return`
- Bootstrap com inicializacao de `SP=256` e chamada de `Sys.init`
- Entrada por diretorio com multiplos arquivos `.vm`
- Prefixo correto para variaveis `static` em traducao multi-arquivo

## Como Compilar

Na raiz do projeto:

```powershell
mvn clean package
```

O arquivo executavel sera gerado em:

```text
target/vmtranslator-1.0.0.jar
```

## Como Executar

Use o `.jar` gerado, passando um arquivo `.vm` ou um diretorio como entrada:

```powershell
java -jar target\vmtranslator-1.0.0.jar caminho\para\Arquivo.vm
java -jar target\vmtranslator-1.0.0.jar caminho\para\Diretorio
```

Quando a entrada e um arquivo `.vm`, o tradutor gera um `.asm` com o mesmo nome.
Quando a entrada e um diretorio, todos os `.vm` diretos desse diretorio sao
traduzidos em ordem alfabetica para `<nomeDoDiretorio>.asm`.

Para diretorios que contem `Sys.vm`, o tradutor escreve o bootstrap antes dos
comandos VM. Isso atende os testes com `Sys.init` sem interferir nos testes que
ja inicializam a pilha pelo script `.tst`.

## Exemplo de Uso

```powershell
java -jar target\vmtranslator-1.0.0.jar projects\07\StackArithmetic\SimpleAdd\SimpleAdd.vm
java -jar target\vmtranslator-1.0.0.jar projects\08\FunctionCalls\NestedCall
```

Saida gerada:

```text
projects/07/StackArithmetic/SimpleAdd/SimpleAdd.asm
projects/08/FunctionCalls/NestedCall/NestedCall.asm
```

## Como Validar no CPUEmulator

1. Compile o projeto com `mvn clean package`.
2. Execute o VMTranslator para gerar o arquivo `.asm` do teste desejado.
3. Abra o CPUEmulator do nand2tetris.
4. Carregue o script `.tst` correspondente, por exemplo:

```text
projects/07/StackArithmetic/SimpleAdd/SimpleAdd.tst
```

5. Execute o script.

O teste passou quando aparecer a mensagem:

```text
End of script - Comparison ended successfully
```

## Testes Validados

Os seguintes testes do Project 07 foram usados para validar a implementacao:

- `StackArithmetic/SimpleAdd`
- `StackArithmetic/StackTest`
- `MemoryAccess/BasicTest`
- `MemoryAccess/PointerTest`
- `MemoryAccess/StaticTest`

Optamos por Java 17 por familiaridade da equipe e por permitir uma organização modular do tradutor (parser e gerador de código). A linguagem oferece tipagem estática, boa manipulação de arquivos e integração com Maven e JUnit, o que facilita testes e manutenção do projeto.
