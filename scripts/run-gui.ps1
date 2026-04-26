$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot

& (Join-Path $PSScriptRoot "compile.ps1")
java -cp (Join-Path $projectRoot "out") com.securetransmission.AppLauncher
