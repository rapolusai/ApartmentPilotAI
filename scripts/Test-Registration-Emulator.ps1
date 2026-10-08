[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5580')
if(!$AllowSyntheticRecords){throw 'Allow retained synthetic localhost fixtures explicitly.'}
. "$PSScriptRoot/Test-Login-Emulator.ps1" -AllowSyntheticRecords -HelpersOnly -EmulatorSerial $EmulatorSerial
function Fill([string]$label,[string]$value) {
 Observe
 $node=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]
 if(!$node){ScrollDown; $node=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]}
 if(!$node -or $node.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw "Visible input not found: $label"}
 Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
 # Select all through the Android keyboard shortcuts; only synthetic inputs are used.
 Device @('shell','input','keycombination','113','29')
 Device @('shell','input','text',$value.Replace(' ','%s'))
 Device @('shell','input','keyevent','4')
 Observe
}
function ClickReachable([string]$label) {
 Observe
 if(!@($script:screen.SelectNodes('//node')|Where-Object {($_.text -eq $label -or $_.'content-desc' -eq $label) -and $_.clickable -eq 'true'}).Count){ScrollDown}
 Click $label
}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString();$adminMobile=Mobile;$residentMobile=Mobile
Device @('shell','am','force-stop',$package)
Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
Observe
if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Home'}).Count){Click 'More';ScrollDown;Click 'Sign out';Click 'Sign out'}
elseif(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Switch login'}).Count){Click 'Switch login'}
Click 'Admin login';Click 'New apartment? Set up'
ClickReachable 'Continue'
AssertScreen 'Confirm you are authorised to manage this apartment.' 'Admin authority consent required before proceeding'
Fill 'Your name' 'REG UI Admin';Fill 'Mobile number' $adminMobile;Fill 'Create PIN' $pin
ClickReachable 'I am authorised to manage this apartment.'
ClickReachable 'Continue'
AssertScreen 'Apartment details' 'Valid Admin details advance to apartment setup'
Fill 'Apartment name' "REG UI $stamp";Fill 'Address' 'Synthetic Street 1';Fill 'PIN code' '500001';Fill 'Number of flats · 5–50' '5'
ClickReachable 'Create apartment'
AssertScreen 'Your community is ready.' 'Real native setup creates apartment successfully'
AssertScreen "REG UI $stamp" 'Success screen identifies server apartment'
$admin=Api POST '/auth/login' @{mobile=$adminMobile;pin=$pin;role='ADMIN'}
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $admin.token
ClickReachable 'Open dashboard';Click 'More';ScrollDown;Click 'Sign out';Click 'Sign out'
Click 'Resident login';Click 'New resident? Join'
Fill 'Invite code' $invite.code;ClickReachable 'Find apartment'
AssertScreen "REG UI $stamp" 'Native invite lookup uses real apartment identity'
ClickReachable 'Choose flat'
AssertScreen 'Confirm your agreement to join.' 'Resident consent required before proceeding'
Fill 'Your name' 'REG UI Resident';Fill 'Mobile number' $residentMobile;Fill 'Create PIN' $pin
ClickReachable 'I agree to share my details with the apartment admin.';ClickReachable 'Choose flat'
AssertScreen 'Make yourself at home.' 'Valid Resident details reach native flat grid'
ClickReachable 'Request to join';AssertScreen 'Choose a flat.' 'Resident must explicitly select a flat'
Click 'Choose flat A-101';ClickReachable 'Request to join'
AssertScreen 'Almost home.' 'Real native join stays pending'
AssertScreen 'Pending' 'Native pending screen shows server status'
Device @('shell','am','force-stop',$package)
Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
AssertScreen 'Almost home.' 'Encrypted join receipt survives process restart without PIN'
Click 'Check status';AssertScreen 'Pending' 'Check status retrieves real pending claim'
$members=Api GET '/members' $null $admin.token
$member=@($members|Where-Object {$_.mobile -eq $residentMobile})[0]
Api POST "/members/$($member.id)/approve" @{verified=$true} $admin.token | Out-Null
Click 'Check status';AssertScreen 'Welcome home!' 'Native Check status observes real Admin approval'
Click 'Back to sign in';Credentials $residentMobile $pin;Click 'Sign in'
AssertScreen 'Home' 'Approved native Resident can sign in'
AssertScreen "REG UI $stamp" 'Resident Home stays in its server-derived apartment'
Write-Host "Completed $script:checks bounded native registration checks. REG UI $stamp retained; no credentials printed."
