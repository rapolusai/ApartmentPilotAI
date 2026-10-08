[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5580')
if(!$AllowSyntheticRecords){throw 'Allow retained labelled localhost fixtures explicitly.'}
. "$PSScriptRoot/Test-Login-Emulator.ps1" -AllowSyntheticRecords -HelpersOnly -EmulatorSerial $EmulatorSerial
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString();$mobile=Mobile
$owner=Api POST '/auth/register' @{name='APPROVAL UI Admin';mobile=$mobile;pin=$pin;apartmentName="APPROVAL UI $stamp";city='Hyderabad';flats=5}
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $owner.token
$residentMobile=Mobile
Api POST '/auth/join' @{invite=$invite.code;flatLabel='A-101';name='APPROVAL UI Resident';mobile=$residentMobile;pin=$pin;residentType='OWNER'} | Out-Null
$members=Api GET '/members' $null $owner.token
$member=@($members|Where-Object {$_.mobile -eq $residentMobile})[0]
Device @('shell','am','force-stop',$package)
Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
AssertScreen 'Home' 'Approval test starts from authenticated native Home'
Click 'More';ScrollDown;Click 'Sign out';Click 'Sign out'
Click 'Admin login';Credentials $mobile $pin;Click 'Sign in'
AssertScreen "APPROVAL UI $stamp" 'Approval fixture Admin signs into its own apartment'
Click 'Community';Click 'Members';Click 'Join requests';Click 'APPROVAL UI Resident'
AssertScreen 'Review resident' 'Admin can review the pending flat claimant'
Click 'Approve resident'
AssertScreen 'I verified this resident and their flat.' 'Approval dialog requires explicit person/flat verification'
Observe
$submit=@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Approve resident' -and $_.class -like '*Button'})[-1]
if(!$submit -or $submit.enabled -ne 'false'){throw 'Unverified approval submit must be disabled'}
$script:checks++;Write-Host 'PASS: Approval submit disabled before verification'
Click 'Cancel'
$pending=Api GET "/members/$($member.id)" $null $owner.token
if($pending.status -ne 'PENDING'){throw 'Cancelled dialog changed status'}
$script:checks++;Write-Host 'PASS: Cancelled approval leaves server request pending'
Click 'Approve resident';Click 'I verified this resident and their flat.'
Observe
$submit=@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Approve resident' -and $_.class -like '*Button'})[-1]
if($submit.enabled -ne 'true'){throw 'Verification did not enable submit'}
$script:checks++;Write-Host 'PASS: Verification enables approval submit'
Click 'Approve resident'
AssertScreen 'Active' 'Native verified approval refreshes to active server state'
$approved=Api GET "/members/$($member.id)" $null $owner.token
if($approved.status -ne 'ACTIVE'){throw 'Approved status not persisted'}
$script:checks++;Write-Host 'PASS: Native approval persisted in database-backed member detail'
$audit=Api GET '/audit' $null $owner.token
if(!@($audit|Where-Object {$_.action -eq 'RESIDENT_APPROVED' -and $_.recordId -eq $member.id}).Count){throw 'Approval audit not found'}
$script:checks++;Write-Host 'PASS: Native approval has a tenant-scoped audit event'
Device @('shell','screencap','-p','/sdcard/approval-native-final.png')
Device @('pull','/sdcard/approval-native-final.png',(Join-Path $results 'approval-native-final.png'))
Write-Host "Completed $script:checks bounded native approval checks. APPROVAL UI $stamp retained. Credentials not printed."
