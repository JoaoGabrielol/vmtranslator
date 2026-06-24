# Compila o VMTranslator sem Maven (so precisa do JDK 17 no PATH).
$ErrorActionPreference = "Stop"

$root = $PSScriptRoot
$classes = Join-Path $root "target\classes"
$jar = Join-Path $root "target\vmtranslator-1.0.0.jar"

New-Item -ItemType Directory -Force -Path $classes | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $root "target") | Out-Null

$sources = @(
    (Join-Path $root "src\main\java\VMTranslator.java"),
    (Join-Path $root "src\main\java\parser\Command.java"),
    (Join-Path $root "src\main\java\parser\CommandType.java"),
    (Join-Path $root "src\main\java\parser\Parser.java"),
    (Join-Path $root "src\main\java\codewriter\CodeWriter.java")
)

Write-Host "Compilando..."
javac -encoding UTF-8 -d $classes @sources
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Gerando JAR..."
jar --create --file $jar --main-class VMTranslator -C $classes .
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "Pronto: $jar"
Write-Host ""
Write-Host "Exemplo:"
Write-Host "  java -jar target\vmtranslator-1.0.0.jar projects\08\ProgramFlow\BasicLoop"
