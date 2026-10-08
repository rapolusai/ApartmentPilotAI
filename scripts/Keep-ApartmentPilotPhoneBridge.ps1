[CmdletBinding()]
param(
    [string]$DeviceSerial = '5ee2fea',
    [int]$PhonePort = 8080,
    [int]$BackendPort = 8081,
    [switch]$Once
)

$ErrorActionPreference = 'SilentlyContinue'
$adb = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
if (-not (Test-Path -LiteralPath $adb)) {
    throw "Android platform-tools adb.exe was not found at '$adb'."
}

while ($true) {
    try {
        $devices = & $adb devices 2>$null
        $isOnline = $devices | Where-Object {
            $_ -match ('^' + [regex]::Escape($DeviceSerial) + '\s+device(?:\s|$)')
        }

        if ($isOnline) {
            $mappings = & $adb -s $DeviceSerial reverse --list 2>$null
            $expectedMapping = "tcp:$PhonePort tcp:$BackendPort"
            if (-not ($mappings | Where-Object { $_ -match [regex]::Escape($expectedMapping) })) {
                & $adb -s $DeviceSerial reverse "tcp:$PhonePort" "tcp:$BackendPort" 2>$null | Out-Null
                if ($LASTEXITCODE -eq 0) {
                    Write-Output "Restored ApartmentPilotAI phone bridge: tcp:$PhonePort -> tcp:$BackendPort"
                }
            }
        }
    }
    catch {
        # A disconnected or restarting ADB transport is retried on the next poll.
    }

    if ($Once) { break }
    Start-Sleep -Seconds 3
}
