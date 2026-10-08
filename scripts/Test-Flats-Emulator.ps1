[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5580')
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'Allow retained labelled localhost test fixtures explicitly.'}
. "$PSScriptRoot/Test-Login-Emulator.ps1" -AllowSyntheticRecords -HelpersOnly -EmulatorSerial $EmulatorSerial
function Check([bool]$condition,[string]$label) {if(!$condition){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function AssertTitle([string]$title,[string]$label) {Observe;if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.'resource-id' -like '*:id/title' -and $_.text -eq $title}).Count){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function PostCommand([string]$path,[hashtable]$body,[string]$token='') {if(!$body.ContainsKey('requestKey')){$body.requestKey=[guid]::NewGuid().ToString()};Api POST $path $body $token}
function Fill([string]$label,[string]$value) {
 Observe
 $field=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]
 if(!$field){throw "Visible native input not found: $label"}
 if($field.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid input bounds'}
 Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
 Device @('shell','input','keycombination','113','29')
 if($value){Device @('shell','input','text',$value.Replace(' ','%s'))}else{Device @('shell','input','keyevent','67')}
 Device @('shell','input','keyevent','4');Observe
}
function HasFlat([string]$label) {Observe;return @($script:screen.SelectNodes('//node')|Where-Object {$_.'content-desc' -eq "Open flat $label"}).Count -gt 0}
function SelectPopupItem([string]$label) {
 Observe
 $item=@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq $label})[0]
 if(!$item -or $item.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw "Popup item not found: $label"}
 Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
 Observe
}

# Verify the already signed-in emulator account is the retained synthetic apartment fixture before changing its session.
Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
Observe
$screenTitle=@($script:screen.SelectNodes('//node')|Where-Object {$_.'resource-id' -like '*:id/title'})[0].text
if($screenTitle -ne 'More'){Click 'More'}
AssertTitle 'More' 'Flats test opens the account menu on the retained emulator session'
Observe
$synthetic=@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'PROFILE UI Admin' -or $_.text -like 'PROFILE UI Admin *' -or $_.text -like 'FLATS UI Admin *'})
if(!$synthetic.Count){throw 'Refusing to replace emulator session: expected a labelled synthetic test apartment.'}

# Create a separate, labelled tenant with two blocks and a real pending resident claim.
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString();$mobile=Mobile
$owner=Api POST '/auth/register' @{name="FLATS UI Admin $stamp";mobile=$mobile;pin=$pin;apartmentName="FLATS UI $stamp";city='Hyderabad';flats=5;address='Native grid test address';pinCode='500001'}
if(!$owner.token){throw 'Synthetic Admin registration failed.'}
$block=PostCommand '/ops/blocks' @{name='B'} $owner.token
$flat=PostCommand '/ops/flats' @{label='B-201';block='B';floor=2;occupied=$true;active=$true} $owner.token
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $owner.token
$residentName="FLATS UI Resident $stamp";$residentMobile=Mobile
$claim=Api POST '/auth/join' @{invite=$invite.code;flatLabel='B-201';name=$residentName;mobile=$residentMobile;pin=$pin;residentType='TENANT'}
if(!$block.id -or !$flat.id -or !$claim.receipt){throw 'Could not prepare the labelled flat and pending resident fixtures.'}

# Replace only the verified synthetic emulator account; no physical device is targeted.
ScrollDown;Click 'Sign out';Click 'Sign out'
AssertScreen 'Choose login' 'Synthetic prior Admin signs out before the isolated Flats fixture'
Click 'Admin login';Credentials $mobile $pin;Click 'Sign in'
AssertScreen "FLATS UI $stamp" 'Flats Admin authenticates to the isolated tenant'
Click 'Community'
Observe
for($attempt=0;$attempt -lt 4 -and !@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Apartment'}).Count;$attempt++){ScrollDown}
if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Apartment'}).Count){throw 'Admin Apartment entry is not reachable from Community.'}
Click 'Apartment';Observe
for($attempt=0;$attempt -lt 3 -and !@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Flats'}).Count;$attempt++){ScrollDown}
if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Flats'}).Count){throw 'Flats entry is not reachable from Apartment administration.'}
Click 'Flats'
AssertScreen 'Search flat or resident' 'Native Flats page provides flat/resident search'
AssertScreen 'Filter by block' 'Native Flats page provides a block filter'
Check (HasFlat 'B-201') 'Searchable flat grid contains the new flat'
Check (HasFlat 'A-101') 'All block selection includes the original block'

# Exercise block filtering and both search keys, including the empty state.
Click 'Filter by block';SelectPopupItem 'B'
Check (HasFlat 'B-201') 'Block B filter retains its flat'
Check (!(HasFlat 'A-101')) 'Block B filter excludes Block A flats'
Click 'Filter by block';SelectPopupItem 'All'
Fill 'Search flat or resident' 'FLATS UI Resident'
Check (HasFlat 'B-201') 'Resident-name search finds the matching flat'
Check (!(HasFlat 'A-101')) 'Resident-name search excludes nonmatching flats'
Check (HasFlat 'B-201') 'Resident-name result opens the expected flat'
Fill 'Search flat or resident' 'no-such-flat-identity'
AssertScreen 'No matching flats' 'Unmatched search presents a useful empty state'
Fill 'Search flat or resident' 'B-201'
Check (HasFlat 'B-201') 'Flat-number search finds the intended flat'

# Open the server-backed detail record and verify fields/actions, then test nested and Android Back navigation.
Click 'Open flat B-201'
AssertScreen 'Flat details' 'Selected grid row opens the separate details page'
AssertScreen 'B-201' 'Flat details show the server-selected flat label'
AssertScreen 'Block' 'Flat details show block information'
AssertScreen 'Floor' 'Flat details show floor information'
AssertScreen 'Occupied' 'Flat details show occupancy'
AssertScreen $residentName 'Flat details show the server-linked resident'
AssertScreen 'pending' 'Flat detail account state reflects the pending claim'
AssertScreen 'Edit flat' 'Flat details expose the existing Edit action'
AssertScreen 'Payment history' 'Flat details expose scoped payment history'
Observe
Check (!@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Resident handover'}).Count) 'Pending accounts do not expose Active-only handover'
Device @('shell','screencap','-p','/sdcard/flat-details-final.png')
Device @('pull','/sdcard/flat-details-final.png',(Join-Path $results 'flat-details-final.png'))
Click 'Edit flat';AssertScreen 'Flat number' 'Existing edit form opens from selected detail'
AssertScreen 'B-201' 'Edit form is prefilled from persisted flat data'
Device @('shell','input','keyevent','4');AssertScreen 'Edit flat' 'Android Back returns from edit form to the same flat detail'
Device @('shell','input','keyevent','4');AssertScreen 'Search flat or resident' 'Android Back returns to the searchable grid'
Check (HasFlat 'B-201') 'Search draft survives detail and edit navigation'
Write-Host "Completed $script:checks bounded native Flats checks. FLATS UI $stamp retained. Credentials not printed."
