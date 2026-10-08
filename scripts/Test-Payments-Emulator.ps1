[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[switch]$ResidentStatusContinuationOnly,[ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5580')
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'Allow retained labelled localhost test fixtures explicitly.'}
. "$PSScriptRoot/Test-Login-Emulator.ps1" -AllowSyntheticRecords -HelpersOnly -EmulatorSerial $EmulatorSerial
function Check([bool]$condition,[string]$label) {if(!$condition){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function AssertTitle([string]$title,[string]$label) {Observe;if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.'resource-id' -like '*:id/title' -and $_.text -eq $title}).Count){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function AssertCurrentSyntheticSession {
    Observe
    if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Choose login'}).Count){return $false}
    $title=@($script:screen.SelectNodes('//node')|Where-Object {$_.'resource-id' -like '*:id/title'})[0]
    if(!$title -or $title.text -ne 'More'){Click 'More'}
    Observe
    $profile=@($script:screen.SelectNodes('//node')|Where-Object {$_.text -match '^(FLATS UI|PROFILE UI|PAYMENTS UI|LOGIN ROLE UI) (Admin|Resident|Treasurer)( |$)'})
    if(!$profile.Count){throw 'Refusing to sign out a non-synthetic emulator account.'}
    return $true
}
function SignOutSynthetic {
    if(AssertCurrentSyntheticSession){
        ScrollDown;Click 'Sign out';Click 'Sign out'
        AssertScreen 'Choose login' 'Synthetic emulator account signs out before isolated payment test'
    }
}
function ClickContaining([string]$fragment) {
    Observe
    foreach($label in @($script:screen.SelectNodes('//node')|Where-Object {$_.text -like "*$fragment*" -or $_.'content-desc' -like "*$fragment*"})){
        $target=$label
        while($target -and $target.Name -eq 'node' -and $target.clickable -ne 'true'){$target=$target.ParentNode}
        if($target -and $target.Name -eq 'node' -and $target.clickable -eq 'true' -and $target.enabled -eq 'true'){
            if($target.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid bounds'}
            Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
            Observe
            return
        }
    }
    throw "Clickable control containing '$fragment' not found."
}
function Fill([string]$label,[string]$value) {
    Observe
    $field=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]
    if(!$field){throw "Visible native input not found: $label"}
    if($field.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid input bounds'}
    Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
    Device @('shell','input','keycombination','113','29')
    if($value){Device @('shell','input','text',$value.Replace(' ','%s'))}else{Device @('shell','input','keyevent','67')}
    Device @('shell','input','keyevent','4')
    Observe
}
function LoginRole([string]$role,[string]$mobile,[string]$pin,[string]$apartment) {
    Click "$role login";Credentials $mobile $pin;Click 'Sign in'
    AssertScreen $apartment "$role account signs in to the fixture apartment"
    AssertTitle 'Home' "$role arrives at Home"
}
function OpenApprovals([string]$rowFragment) {
    Click 'Money';AssertTitle 'Money' 'Money navigation opens'
    Click 'Payments';AssertTitle 'Payment approvals' 'Staff reaches payment approvals'
    ClickContaining $rowFragment;AssertTitle 'Payment' 'Selected payment opens its review details'
}
function SubmitResidentPayment([string]$amount,[string]$reference) {
    AssertTitle 'Home' 'Resident starts payment from Home'
    Click "I've paid";AssertTitle 'Payment details' 'Maintenance declaration form opens'
    Fill 'Amount paid' $amount
    Fill 'Transaction reference · required except cash' $reference
    Click 'Submit payment'
    $beforeConfirmation=Api GET '/payments' $null $residentToken
    Check (@($beforeConfirmation|Where-Object {$_.reference -eq $reference}).Count -eq 0) 'Unchecked declaration confirmation prevents a payment write'
    Click 'I have already made this payment.'
    Click 'Submit payment';AssertTitle 'Payment status' 'Native submission opens persistent status page'
    AssertScreen 'Sent for review' 'Submitted declaration is explicitly pending review'
    ScrollDown
    AssertScreen 'Payment proof' 'Pending declaration exposes its proof action'
}

if($ResidentStatusContinuationOnly){
    Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
    AssertTitle 'Payment status' 'Continuation starts on the already-signed-in rejected payment'
    AssertScreen 'Payment not found: Please verify the transaction reference.' 'Resident sees the full server-saved rejection reason'
    Click 'Resubmit details';AssertTitle 'Payment details' 'Resident can open a corrected resubmission form'
    AssertScreen 'Amount paid' 'Resubmission form contains the amount field'
    AssertScreen 'Submit payment' 'Resubmission form exposes submit action'
    Device @('shell','input','keyevent','4');AssertTitle 'Payment status' 'Android Back returns from resubmission to payment status'
    Write-Host "Completed $script:checks resident rejection-status continuation checks on $EmulatorSerial. No credentials changed."
    return
}

# Create a separate tenant and role fixtures through the local backend. All records are retained and labelled.
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$month=(Get-Date).ToString('yyyy-MM');$day=(Get-Date).ToString('yyyy-MM-dd')
$adminMobile=Mobile;$residentMobile=Mobile;$treasurerMobile=Mobile
$apartment="PAYMENTS UI $stamp"
$admin=Api POST '/auth/register' @{name="PAYMENTS UI Admin $stamp";mobile=$adminMobile;pin=$pin;apartmentName=$apartment;city='Hyderabad';flats=5}
if(!$admin.token){throw 'Could not create synthetic payment-test Admin.'}
Api POST '/billing/rules' @{amount=3000;effective=$month;billingDay=1;dueDay=10} $admin.token | Out-Null
$generated=Api POST "/billing/generate?month=$month" @{} $admin.token
if($generated.created -lt 5){throw 'The fixture did not generate maintenance bills for all flats.'}
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $admin.token
if(!$invite.code){throw 'Could not create the synthetic member invite.'}
$resident=Api POST '/auth/join' @{invite=$invite.code;flatLabel='A-101';name="PAYMENTS UI Resident $stamp";mobile=$residentMobile;pin=$pin;residentType='OWNER'}
$treasurer=Api POST '/auth/join' @{invite=$invite.code;flatLabel='A-102';name="PAYMENTS UI Treasurer $stamp";mobile=$treasurerMobile;pin=$pin;residentType='OWNER'}
$members=Api GET '/members' $null $admin.token
foreach($mobile in @($residentMobile,$treasurerMobile)){
    $member=@($members|Where-Object {$_.mobile -eq $mobile})[0]
    if(!$member){throw 'A synthetic member fixture was not persisted.'}
    Api POST "/members/$($member.id)/approve" @{verified=$true} $admin.token | Out-Null
    if($mobile -eq $treasurerMobile){Api POST "/members/$($member.id)/treasurer" @{} $admin.token | Out-Null}
}
$loginResident=Api POST '/auth/login' @{mobile=$residentMobile;pin=$pin;role='RESIDENT'}
$loginTreasurer=Api POST '/auth/login' @{mobile=$treasurerMobile;pin=$pin;role='TREASURER'}
if(!$loginResident.token -or !$loginTreasurer.token){throw 'Fixture roles could not authenticate with their persisted credentials.'}
$residentToken=$loginResident.token;$treasurerToken=$loginTreasurer.token
$bills=Api GET "/bills?month=$month" $null $admin.token
$bill=@($bills|Where-Object {$_.flatLabel -eq 'A-101'})[0]
if(!$bill){throw 'The resident fixture has no maintenance bill.'}

# Work only on a named emulator and only replace a known synthetic prior session.
Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
SignOutSynthetic
LoginRole 'Resident' $residentMobile $pin $apartment
SubmitResidentPayment '1000' "PAYTEST-$stamp-1"
$residentRows=Api GET '/payments' $null $residentToken
$first=@($residentRows|Where-Object {$_.reference -eq "PAYTEST-$stamp-1"})[0]
Check ([bool]$first -and $first.status -eq 'PENDING') 'First native declaration persists as pending, not paid'
$ownDetail=Api GET "/payments/$($first.id)" $null $residentToken
Check ($ownDetail.status -eq 'PENDING' -and $ownDetail.canAttach -eq $true) 'Resident status detail remains flat-scoped and attachable while pending'

# Treasurer review verifies the real role and receipt behavior through the native screen.
SignOutSynthetic
LoginRole 'Treasurer' $treasurerMobile $pin $apartment
OpenApprovals 'A-101'
AssertScreen 'Approve' 'Treasurer has the per-payment approval action'
AssertScreen 'Reject' 'Treasurer has the per-payment rejection action'
Click 'Approve';AssertScreen 'Payment verified?' 'Approval requires the explicit confirmation dialog'
Click 'Verified · approve';AssertTitle 'Digital receipt' 'Confirmed Treasurer approval opens the issued receipt'
AssertScreen 'PAID' 'Receipt visibly identifies an approved payment'
$approved=Api GET "/payments/$($first.id)" $null $treasurerToken
Check ($approved.status -eq 'APPROVED' -and !$approved.canAttach -and [bool]$approved.receiptNumber) 'Staff review persists approval and a unique receipt, and locks proof uploads'
$paidBill=Api GET "/bills/$($bill.id)" $null $residentToken
Check ([decimal]$paidBill.paid -eq 1000) 'Approved payment synchronizes the resident bill balance'

# Second payment deliberately exercises native rejection and resident-visible correction details.
SignOutSynthetic
LoginRole 'Resident' $residentMobile $pin $apartment
SubmitResidentPayment '500' "PAYTEST-$stamp-2"
$residentRows=Api GET '/payments' $null $residentToken
$second=@($residentRows|Where-Object {$_.reference -eq "PAYTEST-$stamp-2"})[0]
Check ([bool]$second -and $second.status -eq 'PENDING') 'Second native declaration creates a distinct pending record'
SignOutSynthetic
LoginRole 'Treasurer' $treasurerMobile $pin $apartment
OpenApprovals '500'
Click 'Reject';AssertTitle 'Reject payment' 'Treasurer rejection opens the correction form'
Click 'Reject payment'
$stillPending=Api GET "/payments/$($second.id)" $null $treasurerToken
Check ($stillPending.status -eq 'PENDING') 'Empty rejection message is blocked before the backend write'
Fill 'Message to resident' 'Please verify the transaction reference.'
Click 'Reject payment';AssertTitle 'Payment approvals' 'Valid rejection returns to staff approvals'
$rejected=Api GET "/payments/$($second.id)" $null $treasurerToken
Check ($rejected.status -eq 'REJECTED' -and $rejected.reason -like '*Please verify the transaction reference.*') 'Rejection reason is persisted and available to staff'

SignOutSynthetic
LoginRole 'Resident' $residentMobile $pin $apartment
Click 'More';Click 'Payment history';AssertTitle 'Payment history' 'Resident history opens through native navigation'
ClickContaining '500';AssertTitle 'Payment status' 'Rejected row opens the native resident status detail'
AssertScreen 'Payment not approved' 'Resident sees the rejected state'
AssertScreen 'Payment not found: Please verify the transaction reference.' 'Resident sees the full server-saved rejection reason'
Click 'Resubmit details';AssertTitle 'Payment details' 'Resident can start a corrected resubmission'
AssertScreen 'Submit payment' 'Corrected declaration presents its submit action'

Write-Host "Completed $script:checks bounded native payment checks on $EmulatorSerial. Fixture PAYMENT UI $stamp retained. No credentials printed; no physical device targeted."
