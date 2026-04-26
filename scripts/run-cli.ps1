$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot

if ($args.Count -eq 0) {
    Write-Host "Usage: .\scripts\run-cli.ps1 <encode|decode|capacity> ..."
    exit 1
}

& (Join-Path $PSScriptRoot "compile.ps1")
java -cp (Join-Path $projectRoot "out") com.securetransmission.cli.StegoCli @args
