# Parte 2 — Checklist VMTranslator

**Dupla:** Matheus Ryan Carreiro Costa Correia · João Gabriel de Oliveira Lopes (2020002740)  
**Linguagem:** Java 17 + Maven  
**Última atualização:** 2026-06-24 — Seção 2 pronta para teste CPUEmulator (BasicLoop / FibonacciSeries)

## Divisão de responsabilidades

| Responsável | Escopo |
|---|---|
| **João Gabriel** | Parser estendido, Main (diretório + multi-arquivo), `projects/08/`, `ParserTest`, README |
| **Matheus Ryan** | CodeWriter (fluxo, funções, bootstrap), `CodeWriterTest`, validação CPUEmulator |

---

## 0. Infraestrutura (Matheus)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| Baixar [nand2tetris.zip](https://drive.google.com/file/d/1xZzcMIUETv3u3sdpM_oTJSTetpVee3KZ/view) e copiar `projects/08/` | Matheus | ✅ | `9797bd0` | 2026-06-24 |
| Commit `chore(projects): add Project 08 test fixtures` | Matheus | ✅ | `9797bd0` | 2026-06-24 |

---

## 1. Parser — novos CommandTypes (João)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| `C_LABEL`, `C_GOTO`, `C_IF`, `C_FUNCTION`, `C_CALL`, `C_RETURN` no enum | João | ✅ | `0c7d34a` | 2026-06-24 |
| `label XYZ` → `C_LABEL`, arg1=`XYZ` | João | ✅ | `0c7d34a` | 2026-06-24 |
| `goto XYZ` → `C_GOTO`, arg1=`XYZ` | João | ✅ | `0c7d34a` | 2026-06-24 |
| `if-goto XYZ` → `C_IF`, arg1=`XYZ` | João | ✅ | `0c7d34a` | 2026-06-24 |
| `function fname nLocals` → `C_FUNCTION` | João | ✅ | `0c7d34a` | 2026-06-24 |
| `call fname nArgs` → `C_CALL` | João | ✅ | `0c7d34a` | 2026-06-24 |
| `return` → `C_RETURN` | João | ✅ | `0c7d34a` | 2026-06-24 |
| Testes unitários em `ParserTest.java` | João | ✅ | `5e57534` | 2026-06-24 |
| Commit `feat(parser): add command types for flow control and functions` | João | ✅ | `0c7d34a` | 2026-06-24 |
| Commit `test(parser): add unit tests for Part 2 commands` | João | ✅ | `5e57534` | 2026-06-24 |

**Pronto quando:** `mvn test` passa nos testes do Parser para todos os 6 novos tipos. ✅

---

## 2. Controle de fluxo (Matheus)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| Campo `currentFunction` no CodeWriter | Matheus | ✅ | | 2026-06-24 |
| `writeLabel(String label)` | Matheus | ✅ | | 2026-06-24 |
| `writeGoto(String label)` | Matheus | ✅ | | 2026-06-24 |
| `writeIf(String label)` | Matheus | ✅ | | 2026-06-24 |
| Testes unitários em `CodeWriterTest.java` | Matheus | ✅ | | 2026-06-24 |
| CPUEmulator: `ProgramFlow/BasicLoop` | Matheus | ⬜ | | |
| CPUEmulator: `ProgramFlow/FibonacciSeries` | Matheus | ⬜ | | |
| Commit `feat(codewriter): implement label, goto and if-goto` | Matheus | ⬜ | | |
| Commit `test(codewriter): add flow control unit tests` | Matheus | ⬜ | | |
| Switch `C_LABEL`/`C_GOTO`/`C_IF` no `VMTranslator` | Matheus | ✅ | | 2026-06-24 |
| Suporte a diretório (para `BasicLoop/`, `FibonacciSeries/`) | Matheus | ✅ | | 2026-06-24 |

**Pronto quando:** BasicLoop e FibonacciSeries passam no CPUEmulator.

---

## 3. Sub-rotinas (Matheus)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| `writeFunction(String name, int nLocals)` | Matheus | ⬜ | | |
| `writeCall(String name, int nArgs)` | Matheus | ⬜ | | |
| `writeReturn()` | Matheus | ⬜ | | |
| Testes unitários | Matheus | ⬜ | | |
| CPUEmulator: `FunctionCalls/SimpleFunction` | Matheus | ⬜ | | |
| Commit `feat(codewriter): implement function, call and return` | Matheus | ⬜ | | |

**Pronto quando:** SimpleFunction passa no CPUEmulator.

---

## 4. Bootstrap (Matheus)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| `writeBootstrap()` — SP=256 + goto Sys.init | Matheus | ⬜ | | |
| Commit `feat(codewriter): add bootstrap code for SP init and Sys.init call` | Matheus | ⬜ | | |

---

## 5. Main — múltiplos arquivos (João)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| Aceitar diretório como argumento | João | ✅ | | 2026-06-24 |
| Listar `*.vm` em ordem alfabética | João | ✅ | | 2026-06-24 |
| Saída `<nomeDoDiretorio>.asm` | João | ✅ | | 2026-06-24 |
| `writeBootstrap()` antes de processar VMs | João | ⬜ | | |
| `setFileName(basename)` por arquivo `.vm` | Matheus | ⬜ | | |
| Switch com todos os novos `CommandType` | João | 🟡 | | 2026-06-24 |
| Commit `feat(codewriter): add setFileName for multi-file static symbols` | Matheus | ⬜ | | |
| Commit `feat(main): support directory input and multiple vm files` | João | ⬜ | | |

> 🟡 Switch parcial: apenas `C_LABEL`, `C_GOTO`, `C_IF` (suficiente para ProgramFlow).

---

## 6. Integração final (ambos)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| CPUEmulator: `FunctionCalls/NestedCall` | Matheus | ⬜ | | |
| `mvn test` verde | ambos | ⬜ | | |
| Commit `test: validate ProgramFlow and FunctionCalls in CPUEmulator` | Matheus | ⬜ | | |

---

## 7. README e entregáveis (João)

| Item | Responsável | Status | Commit | Data |
|---|---|---|---|---|
| README com Parte 1 + Parte 2 | João | ⬜ | | |
| Instruções para diretório | João | ⬜ | | |
| Exemplo de saída Parte 2 | João | ⬜ | | |
| Commit `docs(readme): document Part 2 features and directory usage` | João | ⬜ | | |
| Vídeo de demonstração (~10 min) | ambos | ⬜ | | |

---

## Ordem de testes CPUEmulator

1. `projects/08/ProgramFlow/BasicLoop` — ⭐
2. `projects/08/ProgramFlow/FibonacciSeries` — ⭐⭐
3. `projects/08/FunctionCalls/SimpleFunction` — ⭐⭐
4. `projects/08/FunctionCalls/NestedCall` — ⭐⭐⭐ (exige bootstrap)

[Vídeo de testes](https://www.youtube.com/watch?v=nshh6Frnql8)

---

## Comandos

```powershell
# Compilar
mvn clean package

# Testes unitários
mvn test

# Traduzir diretório (Parte 2)
java -jar target\vmtranslator-1.0.0.jar projects\08\ProgramFlow\BasicLoop
java -jar target\vmtranslator-1.0.0.jar projects\08\ProgramFlow\FibonacciSeries

# Validar no CPUEmulator
# Carregar projects\08\ProgramFlow\BasicLoop\BasicLoop.tst
# Esperado: "End of script - Comparison ended successfully"
```

---

## Critério de conclusão

- [ ] Todos os testes em `projects/08/ProgramFlow/` e `projects/08/FunctionCalls/` passam
- [x] `mvn test` verde (Parser Parte 2)
- [ ] README atualizado
- [x] Mínimo 8 commits significativos (3/8+ da Parte 2)
- [ ] Vídeo de demonstração gravado
