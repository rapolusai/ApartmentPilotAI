# This creates synthetic test apartments through HTTP. It never drops/truncates tables.
# Use only against the localhost development backend. Observed runs are recorded in docs/TEST_EVIDENCE.md.
[CmdletBinding()]
param([string]$BaseUrl='http://127.0.0.1:8080/api/v1')
$ErrorActionPreference='Stop'
if(([uri]$BaseUrl).Host -notin @('localhost','127.0.0.1')) { throw 'For safety, this script only targets your local development backend.' }
$script:checks=0
function Api([string]$method,[string]$path,[object]$body=$null,[string]$token='') {
 $h=@{};if($token){$h.Authorization="Bearer $token"}
 $args=@{Method=$method;Uri="$BaseUrl$path";Headers=$h;ContentType='application/json';TimeoutSec=30}
 if($null -ne $body){$args.Body=($body|ConvertTo-Json -Depth 12 -Compress)}
 try {$b=Invoke-RestMethod @args;return [pscustomobject]@{Status=200;Body=$b}}
 catch {if($null -eq $_.Exception.Response){throw};return [pscustomobject]@{Status=[int]$_.Exception.Response.StatusCode;Body=$null}}
}
function Check([bool]$pass,[string]$name){if(!$pass){throw "FAIL: $name"};$script:checks++;Write-Host "PASS: $name"}
function Mobile { return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString() }
$stamp=([guid]::NewGuid().ToString()).Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$mobileA=Mobile;$mobileB=Mobile;$mobileR=Mobile
$s=Api 'GET' '/status';Check ($s.Status -eq 200 -and $s.Body.localRegistration -eq $true) 'Local backend responds; registration explicitly enabled'
$a=Api 'POST' '/auth/register' @{name='TEST Admin A';mobile=$mobileA;pin=$pin;apartmentName="TEST A $stamp";city='Hyderabad';flats=5}
Check ($a.Status -eq 200) 'Create apartment A';$tokenA=$a.Body.token
$b=Api 'POST' '/auth/register' @{name='TEST Admin B';mobile=$mobileB;pin=$pin;apartmentName="TEST B $stamp";city='Bengaluru';flats=5}
Check ($b.Status -eq 200) 'Create isolated apartment B';$tokenB=$b.Body.token
$wrong=Api 'POST' '/auth/login' @{mobile=$mobileA;pin=$pin;role='TREASURER'};Check ($wrong.Status -eq 401) 'Choosing a different login card does not grant its role'
$invite=Api 'POST' '/invites' @{} $tokenA;Check ($invite.Status -eq 200) 'Admin creates resident invite'
$preview=Api 'GET' "/auth/invites/$($invite.Body.code)";Check ($preview.Body.flats.Count -eq 5) 'Invite lists available flats'
$j=Api 'POST' '/auth/join' @{invite=$invite.Body.code;flatLabel='A-101';name='TEST Resident';mobile=$mobileR;pin=$pin;residentType='OWNER'};Check ($j.Body.status -eq 'PENDING') 'Resident starts pending, not active'
$preLogin=Api 'POST' '/auth/login' @{mobile=$mobileR;pin=$pin;role='RESIDENT'};Check ($preLogin.Status -eq 403) 'Pending member cannot sign in'
$members=Api 'GET' '/members' $null $tokenA;$member=$members.Body|Where-Object {$_.mobile -eq $mobileR}
$approved=Api 'POST' "/members/$($member.id)/approve" @{} $tokenA;Check ($approved.Status -eq 200) 'Admin approves resident'
$loginR=Api 'POST' '/auth/login' @{mobile=$mobileR;pin=$pin;role='RESIDENT'};Check ($loginR.Status -eq 200) 'Approved resident signs in';$tokenR=$loginR.Body.token
$month=Get-Date -Format 'yyyy-MM';$day=Get-Date -Format 'yyyy-MM-dd'
$rule=@{amount=2000;effective=$month;billingDay=1;dueDay=10}
$noPrivilege=Api 'POST' '/billing/rules' $rule $tokenR;Check ($noPrivilege.Status -eq 403) 'Resident cannot change maintenance amount'
$ruleResult=Api 'POST' '/billing/rules' $rule $tokenA;Check ($ruleResult.Status -eq 200) 'Admin configures the maintenance amount'
$residentPreview=Api 'GET' "/billing/preview?month=$month" $null $tokenR;Check ($residentPreview.Status -eq 403) 'Resident cannot preview apartment-wide bill generation'
$previewBefore=Api 'GET' "/billing/preview?month=$month" $null $tokenA;Check ($previewBefore.Body.configured -eq $true -and $previewBefore.Body.flats -eq 5 -and $previewBefore.Body.scheduled -eq 10000 -and $previewBefore.Body.existing -eq 0) 'Bill preview uses current rule, eligible flats and database state'
$isolatedPreview=Api 'GET' "/billing/preview?month=$month" $null $tokenB;Check ($isolatedPreview.Body.configured -eq $false -and $isolatedPreview.Body.existing -eq 0) 'Bill preview is tenant isolated'
$gen=Api 'POST' "/billing/generate?month=$month" @{} $tokenA;Check ($gen.Status -eq 200) 'Generate current month bills'
$again=Api 'POST' "/billing/generate?month=$month" @{} $tokenA;Check ($again.Body.created -eq 0) 'Bill generation is idempotent'
$previewAfter=Api 'GET' "/billing/preview?month=$month" $null $tokenA;Check ($previewAfter.Body.existing -eq 5) 'Bill preview reflects generated monthly bills'
$bills=Api 'GET' "/bills?month=$month" $null $tokenA;Check ($bills.Body.Count -eq 5) 'Five unique flat bills'
$own=Api 'GET' "/bills?month=$month" $null $tokenR;Check (@($own.Body).Count -eq 1) 'Resident sees only own flat bill';$bill=@($own.Body)[0]
$foreign=Api 'GET' "/bills/$($bill.id)" $null $tokenB;Check ($foreign.Status -eq 404) 'Cross-apartment bill access is denied'
$residentReminderPreview=Api 'GET' "/billing/reminders/preview?month=$month" $null $tokenR;Check ($residentReminderPreview.Status -eq 403) 'Resident cannot view apartment reminder recipients'
$reminderPreview=Api 'GET' "/billing/reminders/preview?month=$month" $null $tokenA;Check ($reminderPreview.Body.flatCount -eq 5 -and $reminderPreview.Body.recipientCount -eq 1 -and $reminderPreview.Body.externalDeliveryStatus -eq 'NOT_CONFIGURED') 'Reminder preview excludes no eligible unpaid flat and labels external delivery unconfigured'
$reminderKey=[guid]::NewGuid().ToString();$reminder=Api 'POST' "/billing/reminders/send?month=$month" @{requestKey=$reminderKey} $tokenA;Check ($reminder.Body.created -eq 1 -and $reminder.Body.externalDeliveryStatus -eq 'NOT_CONFIGURED') 'Manual reminder creates only an in-app inbox record'
$reminderRetry=Api 'POST' "/billing/reminders/send?month=$month" @{requestKey=$reminderKey} $tokenA;Check ($reminderRetry.Body.created -eq 0 -and $reminderRetry.Body.alreadySent -eq 1) 'Reminder retry is idempotent'
$declaration=@{billId=$bill.id;amount=500;mode='UPI';reference="TEST-$stamp-1";paidOn=$day;note='Synthetic test only';requestKey=[guid]::NewGuid().ToString()}
$p=Api 'POST' '/payments' $declaration $tokenR;Check ($p.Body.status -eq 'PENDING') 'Payment declaration is not approval'
$repeat=Api 'POST' '/payments' $declaration $tokenR;Check ($repeat.Body.id -eq $p.Body.id) 'Payment retry returns the same record'
$preReceipt=Api 'GET' "/receipts/$($p.Body.id)" $null $tokenR;Check ($preReceipt.Status -eq 404) 'Pending payment has no approved receipt'
$noVerify=Api 'POST' '/payments/approve' @{paymentIds=@($p.Body.id);verified=$false} $tokenA;Check ($noVerify.Status -eq 400) 'Approval requires explicit verification'
$verified=Api 'POST' '/payments/approve' @{paymentIds=@($p.Body.id);verified=$true} $tokenA;Check ($verified.Body.approved -eq 1) 'Verified payment approved'
$verifiedAgain=Api 'POST' '/payments/approve' @{paymentIds=@($p.Body.id);verified=$true} $tokenA;Check ($verifiedAgain.Body.approved -eq 0) 'Approval retry does not duplicate receipts'
$receipt=Api 'GET' "/receipts/$($p.Body.id)" $null $tokenR;Check ($receipt.Body.amount -eq 500 -and $receipt.Body.receiptNumber) 'Resident can read approved receipt'
$billNow=Api 'GET' "/bills/$($bill.id)" $null $tokenR;Check ($billNow.Body.paid -eq 500) 'Partial payment retains the balance'
$rejectDeclaration=@{billId=$bill.id;amount=100;mode='UPI';reference="TEST-$stamp-REJECT";paidOn=$day;note='Synthetic rejection';requestKey=[guid]::NewGuid().ToString()};$rejectPayment=Api 'POST' '/payments' $rejectDeclaration $tokenR;Check ($rejectPayment.Body.status -eq 'PENDING') 'A second declaration can enter review after the first is approved'
$residentReject=Api 'POST' "/payments/$($rejectPayment.Body.id)/reject" @{reason='Resident must not reject'} $tokenR;Check ($residentReject.Status -eq 403) 'Resident cannot reject a payment declaration'
$rejected=Api 'POST' "/payments/$($rejectPayment.Body.id)/reject" @{reason='Amount mismatch: correct and resubmit'} $tokenA;Check ($rejected.Status -eq 200) 'Staff rejection stores a corrective reason'
$paymentsAfterReject=Api 'GET' '/payments' $null $tokenR;$rejectedRow=@($paymentsAfterReject.Body|Where-Object {$_.id -eq $rejectPayment.Body.id})[0];Check ($rejectedRow.status -eq 'REJECTED' -and $rejectedRow.reason -like 'Amount mismatch:*') 'Resident sees the persisted rejection reason'
$over=@{billId=$bill.id;amount=1501;mode='CASH';paidOn=$day;note='Excess';requestKey=[guid]::NewGuid().ToString()};$overResult=Api 'POST' '/payments' $over $tokenR;Check ($overResult.Status -eq 400) 'Excess payment rejected'
$immutable=Api 'POST' '/billing/rules' @{amount=2500;effective=$month;billingDay=1;dueDay=10} $tokenA;Check ($immutable.Status -eq 400) 'Current rate change cannot rewrite existing bills'
$expense=@{title='TEST Cleaning';category='Housekeeping';amount=123.45;paidOn=$day;paid=$true;visibleToResidents=$true;requestKey=[guid]::NewGuid().ToString()}
$expenseResult=Api 'POST' '/expenses' $expense $tokenA;Check ($expenseResult.Status -eq 200) 'Paid expense recorded'
$expenseRetry=Api 'POST' '/expenses' $expense $tokenA;Check ($expenseRetry.Body.id -eq $expenseResult.Body.id) 'Expense retry does not double count'
$report=Api 'GET' "/reports/monthly?month=$month" $null $tokenA;Check ($report.Body.closing -eq 376.55) 'Report uses approved payment minus paid expense'
$bulkBills=@($bills.Body|Where-Object {$_.flatLabel -in @('A-102','A-103')});$bulkIds=@();foreach($bulkBill in $bulkBills){$bulkDeclaration=@{billId=$bulkBill.id;amount=100;mode='CASH';reference='';paidOn=$day;note='Synthetic bulk approval';requestKey=[guid]::NewGuid().ToString()};$bulkPayment=Api 'POST' '/payments' $bulkDeclaration $tokenA;$bulkIds+=$bulkPayment.Body.id};Check ($bulkIds.Count -eq 2) 'Two independent payments enter bulk review'
$bulkApproval=Api 'POST' '/payments/approve' @{paymentIds=$bulkIds;verified=$true} $tokenA;Check ($bulkApproval.Body.approved -eq 2) 'Bulk approval verifies two payments atomically'
$bulkRetry=Api 'POST' '/payments/approve' @{paymentIds=$bulkIds;verified=$true} $tokenA;Check ($bulkRetry.Body.approved -eq 0 -and $bulkRetry.Body.alreadyApproved -eq 2) 'Bulk approval retry creates no duplicate receipts'
$notice=Api 'POST' '/notices' @{title='TEST Notice';body='Synthetic notice for this apartment only.';publish=$true} $tokenA;Check ($notice.Status -eq 200) 'Staff publishes notice'
$noticeR=Api 'GET' '/notices' $null $tokenR;Check (@($noticeR.Body|Where-Object {$_.id -eq $notice.Body.id}).Count -eq 1) 'Resident sees published notice'
$noticeB=Api 'GET' '/notices' $null $tokenB;Check (@($noticeB.Body|Where-Object {$_.id -eq $notice.Body.id}).Count -eq 0) 'Other apartment cannot see notice'
$inbox=Api 'GET' '/notifications' $null $tokenR;Check (@($inbox.Body).Count -ge 2) 'Recipient-specific inbox is populated'
$logout=Api 'POST' '/logout' @{} $tokenR;$expired=Api 'GET' '/me' $null $tokenR;Check ($expired.Status -eq 401) 'Logout revokes the server token'
Write-Host "Completed $script:checks HTTP checks. Synthetic tenants TEST A/B $stamp remain in the development database. No data was deleted."
