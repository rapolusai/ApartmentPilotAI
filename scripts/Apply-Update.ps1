# Run FROM THE NEW EXTRACTED PACKAGE, not from inside the working destination.
# Copies source with backups. Never connects to PostgreSQL, stops processes, edits credentials or runs migrations.
[CmdletBinding(SupportsShouldProcess=$true)]
param([string]$Target='D:\ApartmentPilotAI')
$ErrorActionPreference='Stop'
$source=[IO.Path]::GetFullPath((Split-Path $PSScriptRoot -Parent)).TrimEnd('\','/')
$targetPath=[IO.Path]::GetFullPath($Target).TrimEnd('\','/')
if($source -eq $targetPath){throw 'Extract the new ZIP to a separate folder, then run its Apply-Update.ps1 with -Target pointing to your working project.'}
if($source.StartsWith($targetPath+'\',[StringComparison]::OrdinalIgnoreCase) -or $targetPath.StartsWith($source+'\',[StringComparison]::OrdinalIgnoreCase)){throw 'The staging package and destination must not be nested inside one another.'}
if(!(Test-Path (Join-Path $source 'backend\src\main\java\com\rapolus\apartmentpilotai\ApartmentPilotAiApplication.java'))){throw 'This is not the ApartmentPilotAI update package.'}
$existingPom=Join-Path $targetPath 'backend\pom.xml'
if((Test-Path $existingPom) -and (Get-Content -Raw $existingPom) -notmatch 'apartmentpilotai'){throw 'Destination contains a different backend project. No files have been copied.'}
$files=@(Get-ChildItem -LiteralPath $source -File -Recurse)
function Relative([string]$full){return $full.Substring($source.Length+1).Replace('\','/')}
function Preserve([string]$rel){
 return ($rel -match '(^|/)\.idea/' -or $rel -match '(^|/)(build|target|\.gradle|\.tools)/' -or
   $rel -match '(^|/)local\.properties$' -or $rel -match '(^|/)\.env(\.|$)' -or
   $rel -match '^backend/src/main/resources/application.*\.(yml|yaml|properties)$' -or
   $rel -match '^backend/pom\.xml$' -or
   $rel -match '^android/(app/)?build\.gradle(\.kts)?$' -or
   $rel -match '^android/(settings\.gradle(\.kts)?|gradle\.properties|gradlew(\.bat)?)$' -or
   $rel -match '^android/gradle/' -or $rel -match '\.(jks|keystore|iml)$' -or
   $rel -match 'google-services\.json$' -or $rel -match 'service-account')
}
# Verify immutable migration/reference files before changing anything.
foreach($file in $files){
 $rel=Relative $file.FullName;$dest=Join-Path $targetPath $rel
 if((Test-Path -LiteralPath $dest) -and ($rel -match '^backend/src/main/resources/db/migration/' -or $rel -eq 'reference/ApartmentCare_Final_Clickable_UI_v2_1.html')){
  if((Get-FileHash -Algorithm SHA256 -LiteralPath $file.FullName).Hash -ne (Get-FileHash -Algorithm SHA256 -LiteralPath $dest).Hash){throw "Protected existing file differs: $rel. Nothing copied. Keep your existing file and review before applying; never repair by wiping migration history."}
 }
}
# A running backend can load old classes during the copy. Stop it normally in IntelliJ first.
try{$running=@(Get-CimInstance Win32_Process -Filter "Name = 'java.exe' OR Name = 'javaw.exe'" -ErrorAction Stop|Where-Object {$_.CommandLine -like '*com.rapolus.apartmentpilotai.ApartmentPilotAiApplication*' -and $_.CommandLine -like "*$targetPath*"})}catch{$running=@();Write-Warning 'Unable to inspect running Java processes. Confirm the ApartmentPilotAI backend is stopped before continuing.'}
if($running.Count -gt 0){throw 'ApartmentPilotAI backend is still running. Stop that Run configuration in IntelliJ (do not stop PostgreSQL), then re-run this updater.'}
$stamp=Get-Date -Format 'yyyyMMdd-HHmmss-fff'
$backup=Join-Path (Split-Path $targetPath -Parent) ((Split-Path $targetPath -Leaf)+"-backup-$stamp")
$copied=New-Object System.Collections.Generic.List[string]
$preserved=New-Object System.Collections.Generic.List[string]
foreach($file in $files){
 $rel=Relative $file.FullName
 if($rel -match '(^|/)(test-results|\.tools|build|target|\.gradle)/'){continue}
 $dest=Join-Path $targetPath $rel
 if((Test-Path -LiteralPath $dest) -and ((Preserve $rel) -or $rel -match '^backend/src/main/resources/db/migration/' -or $rel -like 'reference/*')){$preserved.Add($rel);continue}
 if($PSCmdlet.ShouldProcess($dest,'Back up existing file and install cumulative increment 02 source')){
  if(Test-Path -LiteralPath $dest){$copy=Join-Path $backup $rel;New-Item -ItemType Directory -Path (Split-Path $copy -Parent) -Force|Out-Null;Copy-Item -LiteralPath $dest -Destination $copy -Force}
  New-Item -ItemType Directory -Path (Split-Path $dest -Parent) -Force|Out-Null
  Copy-Item -LiteralPath $file.FullName -Destination $dest -Force
  $copied.Add($rel)
 }
}
if(!$WhatIfPreference){
 $log=Join-Path $targetPath "docs\update-$stamp.json"
 @{source=$source;target=$targetPath;backup=$backup;copied=@($copied);preserved=@($preserved);databaseModified=$false}|ConvertTo-Json -Depth 6|Set-Content -LiteralPath $log -Encoding UTF8
 Write-Host "Updated $($copied.Count) files. Preserved $($preserved.Count) existing configuration/reference files."
 Write-Host "Source backup: $backup"
 Write-Host "Report: $log"
 Write-Host 'No database was modified. Starting the updated backend applies the new V2 migration.'
 Write-Host 'IntelliJ: rebuild/run. Android Studio: sync and run debug. Existing Gradle/JDK/SDK/environment settings were preserved.'
}
