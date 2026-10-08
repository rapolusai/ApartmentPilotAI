[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5580')
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'Allow labelled localhost synthetic fixtures explicitly.'}
. "$PSScriptRoot/Test-Login-Emulator.ps1" -AllowSyntheticRecords -HelpersOnly -EmulatorSerial $EmulatorSerial
function Check([bool]$condition,[string]$label){if(!$condition){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function AssertTitle([string]$value,[string]$label){Observe;if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.'resource-id' -like '*:id/title' -and $_.text -eq $value}).Count){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function AssertMissingScreen([string]$value,[string]$label){Observe;if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq $value -or $_.'content-desc' -eq $value}).Count){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function LoginRole([string]$role,[string]$mobile,[string]$pin,[string]$apartment){Click "$role login";Credentials $mobile $pin;Click 'Sign in';AssertScreen $apartment "$role account signs into the synthetic reversal apartment";AssertTitle 'Home' "$role account arrives at Home"}
function Fill([string]$label,[string]$value){Observe;$field=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0];if(!$field){throw "Visible native input not found: $label"};if($field.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid input bounds'};Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString());Device @('shell','input','keycombination','113','29');if($value){Device @('shell','input','text',$value.Replace(' ','%s'))}else{Device @('shell','input','keyevent','67')};Device @('shell','input','keyevent','4');Observe}
function ClickContaining([string]$fragment){Observe;foreach($label in @($script:screen.SelectNodes('//node')|Where-Object {$_.text -like "*$fragment*" -or $_.'content-desc' -like "*$fragment*"})){$target=$label;while($target -and $target.Name -eq 'node' -and $target.clickable -ne 'true'){$target=$target.ParentNode};if($target -and $target.Name -eq 'node' -and $target.clickable -eq 'true' -and $target.enabled -eq 'true'){if($target.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid bounds'};Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString());Observe;return}};throw "Clickable control containing '$fragment' not found."}
function ScrollToTop {Observe;$scroll=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -eq 'android.widget.ScrollView'})[0];if(!$scroll -or $scroll.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Visible scroll viewport not found'};$left=[int]$Matches[1];$top=[int]$Matches[2];$right=[int]$Matches[3];$bottom=[int]$Matches[4];$x=([int](($left+$right)/2)).ToString();$from=([int]($top+($bottom-$top)*0.22)).ToString();$to=([int]($top+($bottom-$top)*0.92)).ToString();Device @('shell','input','swipe',$x,$from,$x,$to,'450');Observe}
function SignOutSynthetic {Observe;if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Choose login'}).Count){return};$title=@($script:screen.SelectNodes('//node')|Where-Object {$_.'resource-id' -like '*:id/title'})[0];if(!$title -or $title.text -ne 'More'){Click 'More'};ScrollToTop;if(!@($script:screen.SelectNodes('//node')|Where-Object {$_.text -match '^(FLATS UI|PROFILE UI|PAYMENTS UI|LOGIN ROLE UI|PAYREV)( |$)'}).Count){throw 'Refusing to sign out a non-synthetic emulator account.'};ScrollDown;Click 'Sign out';Click 'Sign out';AssertScreen 'Choose login' 'Prior synthetic emulator session signs out'}

# Setup uses local synthetic identities only. No real payments, credentials output, or row deletion.
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString();$month=(Get-Date).ToString('yyyy-MM');$day=(Get-Date).ToString('yyyy-MM-dd')
$adminMobile=Mobile;$residentMobile=Mobile;$treasurerMobile=Mobile;$apartment="PAYREV UI $stamp"
$admin=Api POST '/auth/register' @{name="PAYREV UI Admin $stamp";mobile=$adminMobile;pin=$pin;apartmentName=$apartment;city='Hyderabad';flats=5}
if(!$admin.token){throw 'Could not create synthetic reversal-test Admin.'}
Api POST '/billing/rules' @{amount=2000;effective=$month;billingDay=1;dueDay=10} $admin.token | Out-Null
Api POST "/billing/generate?month=$month" @{} $admin.token | Out-Null
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $admin.token
if(!$invite.code){throw 'Could not create synthetic reversal-test invitation.'}
foreach($entry in @(@{flat='A-101';name='PAYREV UI Resident';mobile=$residentMobile},@{flat='A-102';name='PAYREV UI Treasurer';mobile=$treasurerMobile})){
 Api POST '/auth/join' @{invite=$invite.code;flatLabel=$entry.flat;name="$($entry.name) $stamp";mobile=$entry.mobile;pin=$pin;residentType='OWNER'} | Out-Null
 $members=Api GET '/members' $null $admin.token;$member=@($members|Where-Object {$_.mobile -eq $entry.mobile})[0]
 if(!$member){throw 'Synthetic reversal-test member was not persisted.'}
 Api POST "/members/$($member.id)/approve" @{verified=$true} $admin.token | Out-Null
 if($entry.flat -eq 'A-102'){Api POST "/members/$($member.id)/treasurer" @{} $admin.token | Out-Null}
}
$billRows=Api GET "/bills?month=$month" $null $admin.token;$bill=@($billRows|Where-Object {$_.flatLabel -eq 'A-101'})[0]
if(!$bill){throw 'Synthetic reversal-test resident bill is missing.'}
$residentLogin=Api POST '/auth/login' @{mobile=$residentMobile;pin=$pin;role='RESIDENT'}
$staffLogin=Api POST '/auth/login' @{mobile=$treasurerMobile;pin=$pin;role='TREASURER'}
if(!$residentLogin.token -or !$staffLogin.token){throw 'Synthetic reversal-test roles failed to authenticate.'}
$residentToken=$residentLogin.token;$treasurerToken=$staffLogin.token
$declaration=Api POST '/payments' @{billId=$bill.id;amount=100;mode='CASH';reference="PAYREV-UI-$stamp";paidOn=$day;note='Synthetic native reversal acceptance';requestKey=[guid]::NewGuid().ToString()} $residentToken
if(!$declaration.id){throw 'Could not create the synthetic payment declaration.'}
$approval=Api POST '/payments/approve' @{paymentIds=@($declaration.id);verified=$true} $admin.token
if($approval.approved -ne 1){throw 'Could not approve the synthetic payment before native reversal testing.'}
$originalReceipt=Api GET "/receipts/$($declaration.id)" $null $residentToken
$expenseBody=@{title="PAYREV UI Expense $stamp";category='Security';amount=50;paidOn=$day;mode='CASH';notes='Synthetic native reversal acceptance';paid=$false;visibleToResidents=$true;requestKey=[guid]::NewGuid().ToString()}
$expense=Api POST '/ops/expenses' $expenseBody $admin.token
if(!$expense.id){throw 'Could not create the synthetic expense draft.'}
$expenseBody.paid=$true;$expenseBody.requestKey=[guid]::NewGuid().ToString()
$expensePaid=Api POST "/ops/expenses/$($expense.id)" $expenseBody $admin.token
if(!$expensePaid.id){throw 'Could not confirm the synthetic paid expense.'}

Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
SignOutSynthetic
LoginRole 'Treasurer' $treasurerMobile $pin $apartment
Click 'Money';AssertTitle 'Money' 'Treasurer opens Money navigation'
Click 'Payments';AssertTitle 'Payment approvals' 'Treasurer opens payment review'
Click 'All payment history';AssertTitle 'Payment history' 'Treasurer opens approved payment history'
ClickContaining '100';AssertTitle 'Payment' 'Treasurer opens the approved payment record'
AssertScreen 'Record reversal' 'Unreversed approved payment exposes the reversal action'
Click 'Record reversal';AssertTitle 'Reverse approved payment' 'Native audited-reversal form opens'
AssertScreen 'Reason for correction / returned funds' 'Reversal form requires an audit reason'
AssertScreen 'I verified the full reversal and cash adjustment' 'Reversal form requires explicit adjustment verification'
Click 'Review reversal'
Check (!(Api GET "/payments/$($declaration.id)" $null $treasurerToken).reversed) 'Blank reason is blocked before any reversal write'
Fill 'Reason for correction / returned funds' ' '
Click 'I verified the full reversal and cash adjustment'
Click 'Review reversal'
Check (!(Api GET "/payments/$($declaration.id)" $null $treasurerToken).reversed) 'Whitespace-only reason is blocked before any reversal write'
Fill 'Reason for correction / returned funds' "PAYREV UI audit $stamp"
Click 'Review reversal';AssertScreen 'Record full reversal?' 'Review presents an explicit irreversible-record confirmation'
Device @('shell','input','keyevent','4')
Check (!(Api GET "/payments/$($declaration.id)" $null $treasurerToken).reversed) 'Android Back cancels confirmation without changing the record'
Click 'Review reversal';Click 'Confirm reversal';AssertTitle 'Payment' 'Confirmed reversal returns to original payment detail'
$reversed=Api GET "/payments/$($declaration.id)" $null $treasurerToken
Check ($reversed.reversed -and $reversed.status -eq 'APPROVED') 'Native confirmation records the audited reversal but preserves original approval'
AssertScreen 'Reversed' 'Original payment detail visibly shows reversed state'
AssertMissingScreen 'Record reversal' 'Reversal action is no longer offered after completion'
$receipt=Api GET "/receipts/$($declaration.id)" $null $residentToken
Check ($receipt.reversed -and $receipt.receiptNumber -eq $originalReceipt.receiptNumber) 'Original resident receipt remains available and marked reversed'
$billAfter=Api GET "/bills/$($bill.id)" $null $residentToken
Check ([decimal]$billAfter.paid -eq 0) 'Native reversal synchronizes the bill balance'

# The same audited form serves paid expenses; verify its client guards and confirmation as well.
Click 'Home';Click 'Money';Click 'Expenses';AssertTitle 'Expenses' 'Treasurer opens expense history'
Click "PAYREV UI Expense $stamp";AssertTitle 'Expense detail' 'Treasurer opens the synthetic paid expense'
AssertScreen 'Record reversal' 'Unreversed paid expense exposes its reversal action'
Click 'Record reversal';AssertTitle 'Reverse paid expense' 'Shared audited-reversal form opens for expenses'
Click 'Review reversal'
Check ([string]::IsNullOrEmpty((Api GET "/ops/expenses/$($expense.id)" $null $admin.token).reversedOn)) 'Expense blank reason is blocked before any reversal write'
Fill 'Reason for correction / returned funds' "PAYREV UI expense audit $stamp"
Click 'Review reversal'
Check ([string]::IsNullOrEmpty((Api GET "/ops/expenses/$($expense.id)" $null $admin.token).reversedOn)) 'Expense missing verification is blocked before any reversal write'
Click 'I verified the full reversal and cash adjustment'
Click 'Review reversal';AssertScreen 'Record full reversal?' 'Expense reversal presents explicit confirmation'
Device @('shell','input','keyevent','4')
Check ([string]::IsNullOrEmpty((Api GET "/ops/expenses/$($expense.id)" $null $admin.token).reversedOn)) 'Android Back cancels expense reversal confirmation'
Click 'Review reversal';Click 'Confirm reversal';AssertTitle 'Expense detail' 'Confirmed expense reversal returns to original expense'
$expenseAfter=Api GET "/ops/expenses/$($expense.id)" $null $admin.token
Check (![string]::IsNullOrEmpty($expenseAfter.reversedOn) -and $expenseAfter.reversalReason -eq "PAYREV UI expense audit $stamp") 'Paid expense remains present with its audited reversal reason'
AssertScreen 'Reversed' 'Expense detail visibly reflects the reversal'
AssertMissingScreen 'Record reversal' 'Expense cannot be reversed a second time from its detail screen'
Write-Host "Completed $script:checks native Android audited-reversal checks on $EmulatorSerial. PAYREV UI $stamp retained; credentials not printed; physical device not targeted."
