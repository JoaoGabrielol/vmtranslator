# Guia para Agente de IA — VMTranslator Parte 2

Use este documento como contexto ao implementar ou revisar a Parte 2 do VMTranslator (nand2tetris Project 08).

## Contexto

- **Projeto:** Tradutor VM → Assembly Hack (curso Compiladores / nand2tetris)
- **Linguagem:** Java 17, Maven, JUnit 5
- **Raiz do código:** `vmtranslator/src/main/java/`
- **Parte 1:** Completa — push/pop, segmentos, aritmética/lógica
- **Parte 2:** Controle de fluxo, sub-rotinas, bootstrap, múltiplos `.vm`

## Divisão da equipe (não alterar escopo sem combinar)

| Pessoa | Arquivos |
|---|---|
| João Gabriel | `Parser.java`, `CommandType.java`, `VMTranslator.java`, `ParserTest.java`, `README.md`, `projects/08/` |
| Matheus Ryan | `CodeWriter.java`, `CodeWriterTest.java` |

## Convenções de código existentes

- `Command` é um `record` com `(CommandType type, String arg1, int arg2, String source, int lineNumber)`
- CodeWriter usa `write(String... lines)` e `writeComment(String)` antes de cada bloco
- Comentários assembly: `// push local 2`, `// label LOOP`, etc.
- Labels internos de comparação (`TRUE0`, `END1`) usam `nextLabel(prefix)` — **não confundir** com labels VM (`funcao$label`)
- Mensagens de erro em português com número da linha
- **Estender** Parser e CodeWriter; não reescrever Parte 1

## Novos CommandTypes

```java
C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_CALL, C_RETURN
```

| Comando VM | Tipo | arg1 | arg2 |
|---|---|---|---|
| `label XYZ` | C_LABEL | XYZ | -1 |
| `goto XYZ` | C_GOTO | XYZ | -1 |
| `if-goto XYZ` | C_IF | XYZ | -1 |
| `function fname nLocals` | C_FUNCTION | fname | nLocals |
| `call fname nArgs` | C_CALL | fname | nArgs |
| `return` | C_RETURN | null/"" | -1 |

## CodeWriter — métodos a implementar

### Campo `currentFunction`

- String, inicialmente vazia ou nome da função atual
- Atualizado em `writeFunction(name, nLocals)`
- Usado para prefixar labels: `currentFunction + "$" + label`

### `setFileName(String name)`

- Necessário para segmento `static` em tradução multi-arquivo
- `staticSymbol(index)` → `fileName + "." + index`
- Chamado pelo Main antes de processar cada `.vm`

### `writeBootstrap()`

```asm
@256
D=A
@SP
M=D
@Sys.init
0;JMP
```

### `writeLabel(String label)`

```asm
(currentFunction$label)
```

### `writeGoto(String label)`

```asm
@currentFunction$label
0;JMP
```

### `writeIf(String label)`

```asm
@SP
AM=M-1
D=M
@currentFunction$label
D;JNE
```

### `writeFunction(String name, int nLocals)`

1. `(name)` — label da função
2. `currentFunction = name`
3. Loop: push `nLocals` zeros na pilha (decrementar contador até 0)

### `writeCall(String name, int nArgs)`

1. Empilhar endereço de retorno: label único `return$N` (contador estático)
2. Empilhar LCL, ARG, THIS, THAT
3. `ARG = SP - 5 - nArgs`
4. `LCL = SP`
5. `goto name`
6. `(return$N)`

Referência nand2tetris para call/return frame na pilha:
`[arg0..argN] [retAddr] [LCL] [ARG] [THIS] [THAT]`

### `writeReturn()`

1. `endFrame = LCL`
2. `retAddr = *(endFrame - 5)`
3. `*(ARG) = pop()` — valor de retorno
4. `SP = ARG + 1`
5. Restaurar THAT, THIS, ARG, LCL de endFrame-1, -2, -3, -4
6. `goto retAddr`

## VMTranslator — fluxo Parte 2

1. Argumento = **caminho de diretório** (não arquivo `.vm` isolado)
2. Listar `*.vm` em ordem alfabética
3. Saída: `<nomeDoDiretorio>.asm` no mesmo diretório
4. `CodeWriter writer = new CodeWriter(output)`
5. `writer.writeBootstrap()`
6. Para cada `.vm`: `writer.setFileName(basename)` → `Parser` → switch em todos os tipos

## Comandos de teste

```powershell
cd vmtranslator
mvn test
mvn clean package
java -jar target\vmtranslator-1.0.0.jar projects\08\ProgramFlow\BasicLoop
java -jar target\vmtranslator-1.0.0.jar projects\08\FunctionCalls\NestedCall
```

Validação manual: CPUEmulator + arquivo `.tst` → `"Comparison ended successfully"`

## Ordem de testes CPUEmulator

1. `ProgramFlow/BasicLoop`
2. `ProgramFlow/FibonacciSeries`
3. `FunctionCalls/SimpleFunction`
4. `FunctionCalls/NestedCall` (requer bootstrap)

## Commits

- Um commit por funcionalidade testada
- Formato: `tipo(escopo): descrição` (feat, test, chore, docs)
- Não commitar `*.asm` (está no `.gitignore`)

## O que NÃO fazer

- Não reescrever Parser/CodeWriter da Parte 1
- Não alterar arquivos do colega sem necessidade de integração
- Não editar o arquivo de plano em `.cursor/plans/`
- Não usar labels VM sem prefixo `funcao$` (colisões entre funções)

## Mapa de arquivos

```
vmtranslator/
├── src/main/java/
│   ├── VMTranslator.java
│   ├── parser/
│   │   ├── Parser.java
│   │   ├── CommandType.java
│   │   └── Command.java
│   └── codewriter/
│       └── CodeWriter.java
├── src/test/java/
│   ├── parser/ParserTest.java
│   └── codewriter/CodeWriterTest.java
├── projects/08/
├── PARTE2_CHECKLIST.md
└── README.md
```

## Referências

- [Project 08 — nand2tetris](https://www.nand2tetris.org/project08)
- [Fixtures zip](https://drive.google.com/file/d/1xZzcMIUETv3u3sdpM_oTJSTetpVee3KZ/view)
- [Vídeo de testes](https://www.youtube.com/watch?v=nshh6Frnql8)
