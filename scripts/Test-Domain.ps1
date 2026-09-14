[CmdletBinding()]
param()
$ErrorActionPreference='Stop'
$root=Split-Path $PSScriptRoot -Parent
$output=Join-Path $root 'test-results\domain-classes'
New-Item -ItemType Directory -Path $output -Force | Out-Null
$sources=@(
 (Join-Path $root 'backend\src\main\java\com\rapolus\apartmentpilotai\domain\Rules.java'),
 (Join-Path $root 'backend\src\main\java\com\rapolus\apartmentpilotai\domain\OperationsRules.java'),
 (Join-Path $root 'backend\src\main\java\com\rapolus\apartmentpilotai\security\Tokens.java'),
 (Join-Path $root 'backend\domain-check\DomainChecks.java'),
 (Join-Path $root 'backend\domain-check\OperationsChecks.java')
)
& javac --release 21 -d $output @sources
if($LASTEXITCODE -ne 0){throw 'Domain compilation failed.'}
& java -cp $output DomainChecks
if($LASTEXITCODE -ne 0){throw 'Original domain checks failed.'}
& java -cp $output OperationsChecks
if($LASTEXITCODE -ne 0){throw 'Operational domain checks failed.'}
Write-Host 'Pure rules only. These checks do not start Spring, PostgreSQL or Android.'
