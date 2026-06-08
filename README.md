# VMTranslator

Implementacao da Parte 1 do VMTranslator, da disciplina de Compiladores.

O projeto traduz comandos da linguagem VM do nand2tetris para Assembly Hack,
contemplando comandos de acesso a memoria e operacoes aritmeticas/logicas.

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

## Escopo Implementado

- Comandos `push` e `pop`
- Segmentos `constant`, `local`, `argument`, `this`, `that`, `temp`, `pointer` e `static`
- Operacoes `add`, `sub`, `neg`, `eq`, `gt`, `lt`, `and`, `or` e `not`

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

Use o `.jar` gerado, passando um arquivo `.vm` como entrada:

```powershell
java -jar target\vmtranslator-1.0.0.jar caminho\para\Arquivo.vm
```

O tradutor gera um arquivo `.asm` na mesma pasta do arquivo `.vm`.

## Exemplo de Uso

```powershell
java -jar target\vmtranslator-1.0.0.jar projects\07\StackArithmetic\SimpleAdd\SimpleAdd.vm
```

Saida gerada:

```text
projects/07/StackArithmetic/SimpleAdd/SimpleAdd.asm
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
