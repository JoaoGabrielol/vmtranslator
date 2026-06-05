# VMTranslator

Repositorio inicial da atividade pratica de implementacao do VMTranslator
Parte 1, da disciplina de Compiladores.

Nesta primeira etapa do repositorio, foram criados apenas a estrutura base do
projeto Java e o inicio do modulo de Parser. A implementacao completa do
tradutor VM para Assembly Hack ainda sera desenvolvida pela dupla ao longo dos
proximos commits.

## Integrantes

- Matheus Ryan Carreiro Costa Correia
- Joao Gabriel de Oliveira Lopes - 2020002740

## Linguagem

- Java 17
- Maven

## O que ja foi feito

- Criacao da estrutura Maven com `pom.xml`
- Criacao do `.gitignore`
- Organizacao inicial dos pacotes:
  - `vmtranslator.parser`
  - `vmtranslator.codewriter`
- Criacao da classe principal `VMTranslator`
- Inicio da classe `Parser`, responsavel por ler comandos de arquivos `.vm`
- Criacao das classes `Command` e `CommandType` para representar comandos VM

## Proximos passos

- Completar a validacao dos comandos VM no Parser
- Implementar o `CodeWriter`
- Gerar arquivos `.asm`
- Adicionar testes conforme as funcionalidades forem implementadas
- Validar a saida no CPUEmulator do nand2tetris

## Escopo da Parte 1

A atividade deve contemplar:

- Comandos `push` e `pop`
- Segmentos `constant`, `local`, `argument`, `this`, `that`, `temp`, `pointer` e `static`
- Operacoes `add`, `sub`, `neg`, `eq`, `gt`, `lt`, `and`, `or` e `not`
