# Downloads build tools only, with checksum verification. Does not install an SDK,
# touch your database, accept Android licenses or change the frozen reference.
[CmdletBinding()]
param()
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$tools = Join-Path $root '.tools'
New-Item -ItemType Directory -Force $tools | Out-Null
function Fetch-VerifiedZip([string]$url,[string]$hashUrl,[string]$algorithm,[string]$zip) {
    Write-Host "Checking $url"
    $response = Invoke-WebRequest -UseBasicParsing -Uri $hashUrl
    $checksumText = if ($response.Content -is [byte[]]) {
        [Text.Encoding]::ASCII.GetString($response.Content)
    } else {
        [string]$response.Content
    }
    $expected = (($checksumText.Trim() -split '\s+')[0]).ToLowerInvariant()
    if ($expected -notmatch '^[0-9a-f]+$') { throw 'Unexpected checksum response.' }
    if (!(Test-Path $zip)) { Invoke-WebRequest -UseBasicParsing -Uri $url -OutFile $zip }
    $actual = (Get-FileHash -Path $zip -Algorithm $algorithm).Hash.ToLowerInvariant()
    if ($actual -ne $expected) { throw "Checksum mismatch: $zip. Remove this downloaded ZIP and retry." }
    return $expected
}
$mvnVersion = '3.9.11'
$mvnDir = Join-Path $tools "apache-maven-$mvnVersion"
if (!(Test-Path (Join-Path $mvnDir 'bin\mvn.cmd'))) {
    $url = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mvnVersion/apache-maven-$mvnVersion-bin.zip"
    $zip = Join-Path $tools 'maven.zip'
    Fetch-VerifiedZip $url "$url.sha512" 'SHA512' $zip | Out-Null
    Expand-Archive -Path $zip -DestinationPath $tools -Force
}
$gradleVersion = '8.13'
$gradleDir = Join-Path $tools "gradle-$gradleVersion"
$gradleZip = Join-Path $tools 'gradle.zip'
$url = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
$sha = Fetch-VerifiedZip $url "$url.sha256" 'SHA256' $gradleZip
if (!(Test-Path (Join-Path $gradleDir 'bin\gradle.bat'))) { Expand-Archive $gradleZip $tools -Force }
$bootstrap = Join-Path $tools 'wrapper-bootstrap'
New-Item -ItemType Directory -Force $bootstrap | Out-Null
Set-Content -Path (Join-Path $bootstrap 'settings.gradle') -Value "rootProject.name='wrapper-bootstrap'" -Encoding Ascii
Set-Content -Path (Join-Path $bootstrap 'build.gradle') -Value '// Wrapper generation only. No Android plugin or SDK required.' -Encoding Ascii
& (Join-Path $gradleDir 'bin\gradle.bat') -p $bootstrap wrapper --gradle-version $gradleVersion --distribution-type bin --gradle-distribution-sha256-sum $sha
if ($LASTEXITCODE -ne 0) { throw 'Gradle wrapper generation failed.' }
$android = Join-Path $root 'android'
Copy-Item (Join-Path $bootstrap 'gradlew') $android -Force
Copy-Item (Join-Path $bootstrap 'gradlew.bat') $android -Force
Copy-Item (Join-Path $bootstrap 'gradle\wrapper\*') (Join-Path $android 'gradle\wrapper') -Force
& (Join-Path $mvnDir 'bin\mvn.cmd') -version
Write-Host 'Tooling ready. Open backend\pom.xml in IntelliJ and android in Android Studio.'
