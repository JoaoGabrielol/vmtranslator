#!/usr/bin/env bash
# Compila o VMTranslator sem Maven (Git Bash / Linux / macOS).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
CLASSES="$ROOT/target/classes"
JAR="$ROOT/target/vmtranslator-1.0.0.jar"

mkdir -p "$CLASSES"

echo "Compilando..."
javac -encoding UTF-8 -d "$CLASSES" \
  "$ROOT/src/main/java/VMTranslator.java" \
  "$ROOT/src/main/java/parser/Command.java" \
  "$ROOT/src/main/java/parser/CommandType.java" \
  "$ROOT/src/main/java/parser/Parser.java" \
  "$ROOT/src/main/java/codewriter/CodeWriter.java"

echo "Gerando JAR..."
jar --create --file "$JAR" --main-class VMTranslator -C "$CLASSES" .

echo ""
echo "Pronto: $JAR"
echo ""
echo "Exemplo:"
echo "  java -jar target/vmtranslator-1.0.0.jar projects/08/ProgramFlow/BasicLoop"
