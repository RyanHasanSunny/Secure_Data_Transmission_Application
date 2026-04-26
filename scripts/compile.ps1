$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$sourceRoot = Join-Path $projectRoot "src"
$outputRoot = Join-Path $projectRoot "out"

if (-not (Test-Path $sourceRoot)) {
    throw "Source directory not found: $sourceRoot"
}

New-Item -ItemType Directory -Force -Path $outputRoot | Out-Null

$javaFiles = Get-ChildItem -Path $sourceRoot -Recurse -Filter *.java | Select-Object -ExpandProperty FullName

if (-not $javaFiles) {
    throw "No Java files were found under $sourceRoot"
}

javac -encoding UTF-8 -d $outputRoot $javaFiles
Write-Host "Compilation completed. Classes written to $outputRoot"
