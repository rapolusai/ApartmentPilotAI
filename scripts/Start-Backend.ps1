[CmdletBinding()]
param([string]$DbUrl='jdbc:postgresql://localhost:5432/partmentpilotai_dev',[string]$DbUser='postgres')
$ErrorActionPreference='Stop'
$root=Split-Path $PSScriptRoot -Parent
$mvn=Join-Path $root '.tools\apache-maven-3.9.11\bin\mvn.cmd'
if (!(Test-Path $mvn)) { throw 'Run .\scripts\Prepare-Tooling.ps1 first, or run the backend using IntelliJ bundled Maven.' }
$oldPassword=$env:DB_PASSWORD;$oldUrl=$env:DB_URL;$oldUser=$env:DB_USER
$secret=Read-Host 'Local PostgreSQL password (not written to any file)' -AsSecureString
$ptr=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($secret)
try { $env:DB_PASSWORD=[Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
$env:DB_URL=$DbUrl;$env:DB_USER=$DbUser
Push-Location (Join-Path $root 'backend')
try { & $mvn spring-boot:run '-Dspring-boot.run.profiles=local';if($LASTEXITCODE -ne 0){throw 'Backend stopped with a non-zero exit code. Review the console.'} }
finally { Pop-Location;$env:DB_PASSWORD=$oldPassword;$env:DB_URL=$oldUrl;$env:DB_USER=$oldUser;$secret.Dispose() }
