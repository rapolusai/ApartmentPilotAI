[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5580')
if(!$AllowSyntheticRecords){throw 'Allow retained labelled localhost fixtures explicitly.'}
. "$PSScriptRoot/Test-Login-Emulator.ps1" -AllowSyntheticRecords -HelpersOnly -EmulatorSerial $EmulatorSerial
function ScrollUp {
 Observe
 $scroll=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -eq 'android.widget.ScrollView'})[0]
 if(!$scroll -or $scroll.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Visible scroll viewport not found'}
 $left=[int]$Matches[1];$top=[int]$Matches[2];$right=[int]$Matches[3];$bottom=[int]$Matches[4]
 Device @('shell','input','swipe',([int](($left+$right)/2)).ToString(),([int]($top+($bottom-$top)*0.25)).ToString(),([int](($left+$right)/2)).ToString(),([int]($top+($bottom-$top)*0.85)).ToString(),'500')
 Observe
}
function SaveProfile {Observe;if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Save'}).Count){ScrollDown};Click 'Save'}
function Fill([string]$label,[string]$value) {
 Observe
 $node=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]
 if(!$node){ScrollDown;$node=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]}
 if(!$node){ScrollUp;$node=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]}
 if(!$node -or $node.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw "Visible input not found: $label"}
 Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
 Device @('shell','input','keycombination','113','29')
 if($value){Device @('shell','input','text',$value.Replace(' ','%s'))}else{Device @('shell','input','keyevent','67')}
 Device @('shell','input','keyevent','4');Observe
}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString();$mobile=Mobile
$owner=Api POST '/auth/register' @{name='PROFILE UI Admin';mobile=$mobile;pin=$pin;apartmentName="PROFILE UI $stamp";city='Pune';flats=5;address='Original test street';pinCode='411001'}
Device @('shell','am','force-stop',$package);Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
AssertScreen 'Home' 'Profile test starts from authenticated Home'
Click 'More';ScrollDown;Click 'Sign out';Click 'Sign out'
Click 'Admin login';Credentials $mobile $pin;Click 'Sign in'
AssertScreen "PROFILE UI $stamp" 'Profile fixture Admin logs in to own apartment'
Click 'Community';ScrollDown;Click 'Apartment';Click 'Apartment details'
AssertScreen 'Apartment profile' 'Native apartment editor opens'
AssertScreen 'Original test street' 'Editor loads persisted address'
Fill 'PIN code' '50001';SaveProfile
AssertScreen 'Enter a 6-digit PIN code.' 'Client rejects invalid PIN code'
$current=Api GET '/ops/apartment' $null $owner.token
if($current.pincode -ne '411001'){throw 'Invalid native form changed server profile'}
$script:checks++;Write-Host 'PASS: Invalid native profile leaves persistence unchanged'
Fill 'PIN code' '500001';Fill 'Address' '';SaveProfile
AssertScreen 'Enter the address (up to 300 characters).' 'Client requires address'
Fill 'Address' 'Updated test street';Fill 'Apartment name' "PROFILE UI updated $stamp";Fill 'City' 'Hyderabad';SaveProfile
AssertScreen "PROFILE UI updated $stamp" 'Native save returns to updated apartment'
$current=Api GET '/ops/apartment' $null $owner.token
if($current.name -ne "PROFILE UI updated $stamp" -or $current.address -ne 'Updated test street' -or $current.pincode -ne '500001' -or $current.city -ne 'Hyderabad'){throw 'Native save did not persist complete profile'}
$script:checks++;Write-Host 'PASS: Complete native profile persists through real API'
Click 'Apartment details';AssertScreen 'Updated test street' 'Reopening editor reloads updated server data'
Device @('shell','input','keyevent','4');AssertScreen "PROFILE UI updated $stamp" 'Android Back returns to apartment without another save'
Device @('shell','screencap','-p','/sdcard/apartment-profile-native-final.png')
Device @('pull','/sdcard/apartment-profile-native-final.png',(Join-Path $results 'apartment-profile-native-final.png'))
Write-Host "Completed $script:checks bounded native apartment profile checks. PROFILE UI $stamp retained. Credentials not printed."
