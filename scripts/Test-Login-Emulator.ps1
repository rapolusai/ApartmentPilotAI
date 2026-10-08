# Native tests restricted to emulator serials. Synthetic credentials remain in memory.
[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[switch]$SessionRestoreOnly,[switch]$OfflineRecoveryOnly,[switch]$ScreensOnly,[switch]$RolesOnly,[switch]$BackAndRotationOnly,[switch]$SameAccountRestoreOnly,[switch]$HelpersOnly,
 [ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5580')
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'Use -AllowSyntheticRecords for retained localhost test fixtures.'}
$adb='C:\Users\rapol\AppData\Local\Android\Sdk\platform-tools\adb.exe'
$psql='C:\Program Files\PostgreSQL\18\bin\psql.exe'
$package='com.rapolus.apartmentpilotai.debug'
$root=Split-Path $PSScriptRoot -Parent
$results=Join-Path $root 'test-results'
if($EmulatorSerial -ne 'emulator-5580') {$results=Join-Path $results "login-$EmulatorSerial";New-Item -ItemType Directory -Force $results | Out-Null}
$script:checks=0
$script:observations=0
$script:dumpRun=[guid]::NewGuid().ToString('N')
function Device([string[]]$commands) {& $adb -s $EmulatorSerial @commands | Out-Null;if($LASTEXITCODE -ne 0){throw 'Emulator command failed.'}}
function Observe {
 for($attempt=0;$attempt -lt 3;$attempt++) {
  $script:observations++
  $dump="/sdcard/login-runtime-$($script:dumpRun)-$($script:observations).xml"
   try {
    Device @('shell','uiautomator','dump','--compressed',$dump)
    Device @('pull',$dump,(Join-Path $results 'login-runtime.xml'))
   $runtimeXml=Join-Path $results 'login-runtime.xml'
   [xml]$script:screen=Get-Content $runtimeXml -Raw
   [xml]$artifact=Get-Content $runtimeXml -Raw
   foreach($inputNode in $artifact.SelectNodes("//node[contains(@class,'EditText') and string-length(@text)>0]")){$inputNode.SetAttribute('text','[REDACTED]')}
   $artifact.Save($runtimeXml)
   return
  } catch {if($attempt -eq 2){throw};Start-Sleep -Milliseconds 500}
 }
}
function Click([string]$text) {
 Observe
 $nodes=@(foreach($label in @($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq $text -or $_.'content-desc' -eq $text})) {
  $target=$label
  while($target -and $target.Name -eq 'node' -and $target.clickable -ne 'true') {$target=$target.ParentNode}
  if($target -and $target.Name -eq 'node' -and $target.clickable -eq 'true' -and $target.enabled -eq 'true') {$target}
 })
 if(!$nodes.Count){throw "Clickable control not found: $text"}
 $bounds=$nodes[-1].bounds
 if($bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid bounds'}
 Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
 Observe
}
function AssertScreen([string]$text,[string]$label) {
 for($attempt=0;$attempt -lt 3;$attempt++) {
  Observe
  if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq $text -or $_.'content-desc' -eq $text}).Count) {
   $script:checks++;Write-Host "PASS: $label"
   return
  }
  if($attempt -lt 2){Start-Sleep -Milliseconds 1000}
 }
 throw "FAIL: $label"
}
function ScrollDown {
 Observe
 $scroll=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -eq 'android.widget.ScrollView'})[0]
 if(!$scroll -or $scroll.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Visible scroll viewport not found'}
 $left=[int]$Matches[1];$top=[int]$Matches[2];$right=[int]$Matches[3];$bottom=[int]$Matches[4]
 $x=([int](($left+$right)/2)).ToString()
 $from=([int]($top+($bottom-$top)*0.85)).ToString();$to=([int]($top+($bottom-$top)*0.25)).ToString()
 Device @('shell','input','swipe',$x,$from,$x,$to,'500')
 Observe
}
function Credentials([string]$mobile,[string]$pin) {
 Observe
 $edits=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText'})
 if($edits.Count -ne 2){throw 'Expected exactly two Login fields'}
 foreach($index in 0,1) {
  Observe
  $edits=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText'})
  $bounds=$edits[$index].bounds
  if($bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid input bounds'}
  Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
  Device @('shell','input','text',$(if($index -eq 0){$mobile}else{$pin}))
 }
 Device @('shell','input','keyevent','4')
 Observe
}
function Api([string]$method,[string]$path,[object]$body=$null,[string]$token='') {
 $args=@{Method=$method;Uri="http://127.0.0.1:8081/api/v1$path";ContentType='application/json';TimeoutSec=30;Headers=@{}}
 if($token){$args.Headers.Authorization="Bearer $token"};if($null -ne $body){$args.Body=$body|ConvertTo-Json -Depth 8 -Compress}
 return Invoke-RestMethod @args
}
function Mobile {return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString()}
if($HelpersOnly){return}
if($BackAndRotationOnly) {
 Click 'Resident login'
 Device @('shell','input','keyevent','4')
 AssertScreen 'Choose login' 'Android Back from login returns chooser'
 Click 'Resident login';Click 'Forgot PIN?'
 Device @('shell','input','keyevent','4')
 AssertScreen 'Resident login' 'Android Back from recovery returns selected role'
 Credentials '9876543210' '1234'
 $originalRotation=(& $adb -s $EmulatorSerial shell settings get system user_rotation).Trim()
 $originalAutoRotation=(& $adb -s $EmulatorSerial shell settings get system accelerometer_rotation).Trim()
 try {
  Device @('shell','settings','put','system','accelerometer_rotation','0')
  $alternateRotation=if($originalRotation -eq '0'){'1'}else{'0'}
  Device @('shell','settings','put','system','user_rotation',$alternateRotation)
  AssertScreen 'Resident login' 'Form rotation retains selected login type'
  Observe
  $edits=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText'})
  if($edits.Count -ne 2 -or $edits[0].text -ne '9876543210'){throw 'Mobile was not retained across recreation'}
  $script:checks++;Write-Host 'PASS: Rotation retains non-secret mobile field'
  if($edits[1].text -notin @('','PIN','4–6 digit PIN')){throw 'PIN must not be restored from saved instance state'}
  $script:checks++;Write-Host 'PASS: Rotation does not persist PIN in saved instance state'
  if($edits[1].password -ne 'true'){throw 'Rotated PIN field must remain masked'}
  $script:checks++;Write-Host 'PASS: Rotated PIN field stays masked'
 } finally {
  Device @('shell','settings','put','system','user_rotation',$originalRotation)
  Device @('shell','settings','put','system','accelerometer_rotation',$originalAutoRotation)
 }
 Write-Host "Completed $script:checks bounded Android Back/rotation checks; original rotation restored."
 return
}
if($RolesOnly) {
 $roleStamp=[guid]::NewGuid().ToString('N').Substring(0,8)
 $rolePin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
 $owner=Api POST '/auth/register' @{name='LOGIN ROLE UI Admin';mobile=(Mobile);pin=$rolePin;apartmentName="LOGIN ROLE UI $roleStamp";city='Hyderabad';flats=5}
 $invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $owner.token
 Observe
 if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Switch login'}).Count){Click 'Switch login'}
 $flatIndex=101
 foreach($role in @('Resident','Treasurer')) {
  $roleMobile=Mobile
  Api POST '/auth/join' @{invite=$invite.code;flatLabel="A-$flatIndex";name="LOGIN ROLE UI $role";mobile=$roleMobile;pin=$rolePin;residentType='OWNER'} | Out-Null
  $members=Api GET '/members' $null $owner.token
  $member=@($members|Where-Object {$_.mobile -eq $roleMobile})[0]
  Api POST "/members/$($member.id)/approve" @{verified=$true} $owner.token | Out-Null
  if($role -eq 'Treasurer'){Api POST "/members/$($member.id)/treasurer" @{} $owner.token | Out-Null}
  Click "$role login"
  Credentials $roleMobile $rolePin
  Click 'Sign in'
  AssertScreen 'Home' "$role real credentials reach native Home"
  AssertScreen "LOGIN ROLE UI $roleStamp" "$role native Home has server-derived apartment"
  Click 'More'
  AssertScreen $role.ToLowerInvariant() "$role native More displays server-derived role"
  Observe
  if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Subscription & billing'}).Count){throw "$role must not expose Admin-only subscription controls"}
  $script:checks++;Write-Host "PASS: $role does not expose Admin-only subscription controls"
  ScrollDown
  Click 'Sign out';Click 'Sign out'
  AssertScreen 'Choose login' "$role native Sign out returns chooser"
  $flatIndex++
 }
 Write-Host "Completed $script:checks real native role checks. LOGIN ROLE UI $roleStamp retained. Credentials not printed."
 return
}
if($ScreensOnly) {
 Observe
 $originalDark=@($script:screen.SelectNodes('//node')|Where-Object {$_.'content-desc' -eq 'Switch to light theme'}).Count -gt 0
 if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Home'}).Count) {
  Click 'More'
  ScrollDown
  Click 'Sign out';Click 'Sign out'
 } elseif(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Switch login'}).Count) {Click 'Switch login'}
 foreach($role in @('Admin','Resident','Treasurer')) {
  Click "$role login"
  AssertScreen "$role login" "$role native login heading is present"
  AssertScreen 'Switch login' "$role login has switch control"
  Observe
  if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.password -eq 'true'}).Count){throw 'PIN must be masked'}
  $script:checks++;Write-Host "PASS: $role PIN field is masked"
  Click 'Forgot PIN?';Click 'Back to sign in'
  AssertScreen "$role login" "$role recovery returns to the same role"
  Click 'Switch login'
 }
 Click 'Admin login'
 if($originalDark){Click 'Switch to light theme'}
 AssertScreen 'Sign in' 'Light phone login exposes Sign in'
 Device @('shell','screencap','-p','/sdcard/login-light-baseline.png')
 Device @('pull','/sdcard/login-light-baseline.png',(Join-Path $results 'login-light-baseline.png'))
 $originalRotation=(& $adb -s $EmulatorSerial shell settings get system user_rotation).Trim()
 $originalAutoRotation=(& $adb -s $EmulatorSerial shell settings get system accelerometer_rotation).Trim()
 try {
  Device @('shell','settings','put','system','accelerometer_rotation','0')
  $alternateRotation=if($originalRotation -eq '0'){'1'}else{'0'}
  Device @('shell','settings','put','system','user_rotation',$alternateRotation)
  AssertScreen 'Admin login' 'Alternate orientation retains selected role'
  ScrollDown
  AssertScreen 'Sign in' 'Alternate orientation Sign in remains reachable'
  Device @('shell','screencap','-p','/sdcard/login-light-alternate.png')
  Device @('pull','/sdcard/login-light-alternate.png',(Join-Path $results 'login-light-alternate.png'))
  Click 'Switch to dark theme'
  AssertScreen 'Sign in' 'Dark alternate orientation Sign in remains reachable'
 } finally {
  Device @('shell','settings','put','system','user_rotation',$originalRotation)
  Device @('shell','settings','put','system','accelerometer_rotation',$originalAutoRotation)
 }
 if(!$originalDark){Click 'Switch to light theme'}
 Write-Host "Completed $script:checks bounded native role/layout checks; emulator original rotation restored."
 return
}
if($OfflineRecoveryOnly) {
 Observe
 if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Home'}).Count){Click 'Home'}
 AssertScreen 'Home' 'Offline test starts in authenticated Home'
 if(Get-NetTCPConnection -LocalPort 18081 -State Listen -ErrorAction SilentlyContinue){throw 'Controlled test port 18081 must be unused'}
 try {
  Device @('reverse','tcp:8080','tcp:18081')
  Device @('shell','am','force-stop',$package)
  Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
  AssertScreen 'Connection paused' 'Unreachable backend shows truthful connection failure'
 } finally {Device @('reverse','tcp:8080','tcp:8081')}
 Click 'Retry connection'
 AssertScreen 'Home' 'Restored backend retries original destination without false login'
 return
}
if($SessionRestoreOnly) {
 Observe
 $apartment=@($script:screen.SelectNodes('//node')|Where-Object {$_.text -like 'LOGIN UI *'})[0].text
 if(!$apartment){throw 'Expected an already signed-in labelled Login UI fixture'}
 Device @('shell','am','force-stop',$package)
 Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
 AssertScreen 'Home' 'Fresh hierarchy confirms session survives process restart'
 AssertScreen $apartment 'Fresh hierarchy confirms restored apartment identity'
 return
}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$mobileA=Mobile;$mobileB=Mobile;$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$a=Api POST '/auth/register' @{name='LOGIN UI Admin A';mobile=$mobileA;pin=$pin;apartmentName="LOGIN UI A $stamp";city='Hyderabad';flats=5}
$b=Api POST '/auth/register' @{name='LOGIN UI Admin B';mobile=$mobileB;pin=$pin;apartmentName="LOGIN UI B $stamp";city='Pune';flats=5}
Device @('reverse','tcp:8080','tcp:8081')
Observe
if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Switch login'}).Count) {Click 'Switch login'}
Observe
if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Choose login'}).Count) {
 Click 'Admin login'
 Credentials $mobileA $pin
 Click 'Sign in'
}
Observe
if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Home'}).Count) {Click 'More'}
ScrollDown
Click 'Sign out'
Click 'Cancel'
AssertScreen 'More' 'Cancelled Sign out retains current account'
Click 'Sign out'
Click 'Sign out'
AssertScreen 'Choose login' 'Confirmed Sign out returns login chooser'
Click 'Admin login'
Click 'Sign in'
AssertScreen 'Enter a valid 10-digit mobile number.' 'Blank login is rejected immediately'
Click 'Forgot PIN?'
AssertScreen 'Reset PIN unavailable' 'Recovery is honestly labelled unconfigured'
Click 'Back to sign in'
Credentials $mobileA $pin
Click 'Sign in'
AssertScreen 'Home' 'Real Admin credentials reach Home'
AssertScreen "LOGIN UI A $stamp" 'Home displays authenticated apartment'
Device @('shell','am','force-stop',$package)
Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
AssertScreen 'Home' 'Encrypted session survives process restart'
AssertScreen "LOGIN UI A $stamp" 'Restored session retains correct apartment'
if([string]::IsNullOrEmpty($env:PGPASSWORD)){throw 'PGPASSWORD required for native true-expiry check'}
$user=[guid]$a.account.id;$tenant=[guid]$a.account.tenantId
& $psql -X -w -h 127.0.0.1 -U postgres -d partmentpilotai_dev -v ON_ERROR_STOP=1 -c "update apartmentpilotai.ap_session set expires_at=now()-interval '1 second' where user_id='$user' and user_id in(select id from apartmentpilotai.ap_user where tenant_id='$tenant' and name='LOGIN UI Admin A')" | Out-Null
if($LASTEXITCODE -ne 0){throw 'Could not expire labelled fixture session'}
Click 'Invite'
AssertScreen 'Sign in again' 'Real expired session routes to reauthentication'
Click 'Sign in'
if($SameAccountRestoreOnly) {
 Credentials $mobileA $pin
 Click 'Sign in'
 AssertScreen 'Invite residents' 'Same account resumes original private destination after expiry'
 AssertScreen 'Invite settings' 'Resumed private destination reloads authenticated server controls'
 Write-Host "Completed $script:checks bounded same-account native checks. LOGIN UI A/B $stamp retained. No private-draft assertion implied."
 return
}
Credentials $mobileB $pin
Click 'Sign in'
AssertScreen 'Home' 'Different account cannot resume previous private destination'
AssertScreen "LOGIN UI B $stamp" 'Changed account displays only its own apartment'
Device @('shell','screencap','-p','/sdcard/login-runtime-final.png')
Device @('pull','/sdcard/login-runtime-final.png',(Join-Path $results 'login-runtime-final.png'))
Write-Host "Completed $script:checks bounded native checks. LOGIN UI A/B $stamp retained. Physical device untouched."
