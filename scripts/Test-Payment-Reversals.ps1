[CmdletBinding()]
param([string]$BaseUrl='http://127.0.0.1:8081/api/v1')
$ErrorActionPreference='Stop'
if(([uri]$BaseUrl).Host -notin @('localhost','127.0.0.1')){throw 'For safety, this script only targets the localhost development backend.'}
$script:checks=0
function Api([string]$method,[string]$path,[object]$body=$null,[string]$token=''){
    $headers=@{};if($token){$headers.Authorization="Bearer $token"}
    $args=@{Method=$method;Uri="$BaseUrl$path";Headers=$headers;ContentType='application/json';TimeoutSec=30}
    if($null -ne $body){$args.Body=($body|ConvertTo-Json -Depth 12 -Compress)}
    try{$r=Invoke-RestMethod @args;return [pscustomobject]@{Status=200;Body=$r}}
    catch{
        if($null -eq $_.Exception.Response){throw}
        $errorBody=$null
        try{$reader=[IO.StreamReader]::new($_.Exception.Response.GetResponseStream());$errorBody=$reader.ReadToEnd();$reader.Dispose()}catch{}
        return [pscustomobject]@{Status=[int]$_.Exception.Response.StatusCode;Body=$null;Error=$errorBody}
    }
}
function Check([bool]$ok,[string]$label){if(!$ok){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function Mobile{return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString()}
function JoinRole([string]$inviteCode,[string]$flat,[string]$name,[string]$mobile,[string]$pin,[string]$adminToken,[switch]$Treasurer){
    $joined=Api POST '/auth/join' @{invite=$inviteCode;flatLabel=$flat;name=$name;mobile=$mobile;pin=$pin;residentType='OWNER'}
    if($joined.Status -ne 200){throw "Could not create labelled test member $flat."}
    $rows=Api GET '/members' $null $adminToken
    $member=@($rows.Body|Where-Object {$_.mobile -eq $mobile})[0]
    if(!$member){throw "Could not find labelled test member $flat."}
    Api POST "/members/$($member.id)/approve" @{verified=$true} $adminToken | Out-Null
    if($Treasurer){Api POST "/members/$($member.id)/treasurer" @{} $adminToken | Out-Null}
}

# Two synthetic tenants verify ownership isolation. All test records are retained; this script never deletes data.
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$month=(Get-Date).ToString('yyyy-MM');$day=(Get-Date).ToString('yyyy-MM-dd')
$owner=Api POST '/auth/register' @{name="PAYREV Admin $stamp";mobile=(Mobile);pin=$pin;apartmentName="PAYREV $stamp";city='Hyderabad';flats=5}
$foreign=Api POST '/auth/register' @{name="PAYREV Foreign Admin $stamp";mobile=(Mobile);pin=$pin;apartmentName="PAYREV OTHER $stamp";city='Bengaluru';flats=5}
if(!$owner.Body.token -or !$foreign.Body.token){Write-Host "Tenant setup diagnostic: owner HTTP $($owner.Status) $($owner.Error); foreign HTTP $($foreign.Status) $($foreign.Error)";throw 'Could not create synthetic reversal test tenants.'}
$adminToken=$owner.Body.token;$foreignToken=$foreign.Body.token
$residentMobile=Mobile;$paidResidentMobile=Mobile;$treasurerMobile=Mobile
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $adminToken
if(!$invite.Body.code){throw 'Could not create synthetic member invitation.'}
JoinRole $invite.Body.code 'A-101' "PAYREV Resident $stamp" $residentMobile $pin $adminToken
JoinRole $invite.Body.code 'A-102' "PAYREV Payment Resident $stamp" $paidResidentMobile $pin $adminToken
JoinRole $invite.Body.code 'A-103' "PAYREV Treasurer $stamp" $treasurerMobile $pin $adminToken -Treasurer
$residentLogin=Api POST '/auth/login' @{mobile=$residentMobile;pin=$pin;role='RESIDENT'}
$paidResidentLogin=Api POST '/auth/login' @{mobile=$paidResidentMobile;pin=$pin;role='RESIDENT'}
$treasurerLogin=Api POST '/auth/login' @{mobile=$treasurerMobile;pin=$pin;role='TREASURER'}
if(!$residentLogin.Body.token -or !$paidResidentLogin.Body.token -or !$treasurerLogin.Body.token){throw 'Synthetic reversal roles could not authenticate.'}
$residentToken=$residentLogin.Body.token;$paidResidentToken=$paidResidentLogin.Body.token;$treasurerToken=$treasurerLogin.Body.token
Api POST '/billing/rules' @{amount=2000;effective=$month;billingDay=1;dueDay=10} $adminToken | Out-Null
Api POST "/billing/generate?month=$month" @{} $adminToken | Out-Null
$bills=Api GET "/bills?month=$month" $null $adminToken
$bill=@($bills.Body|Where-Object {$_.flatLabel -eq 'A-101'})[0]
$paidBill=@($bills.Body|Where-Object {$_.flatLabel -eq 'A-102'})[0]
if(!$bill -or !$paidBill){throw 'A synthetic resident has no current bill.'}

# A pending declaration must not be reversible.
$pending=Api POST '/payments' @{billId=$bill.id;amount=50;mode='UPI';reference="PAYREV-PENDING-$stamp";paidOn=$day;note='Synthetic reversal test';requestKey=[guid]::NewGuid().ToString()} $residentToken
$pendingReverse=Api POST "/ops/payments/$($pending.Body.id)/reverse" @{verified=$true;reason='Pending must not be reversed';requestKey=[guid]::NewGuid().ToString()} $adminToken
Check ($pendingReverse.Status -eq 404) 'Pending payment cannot be reversed'
$pendingDetail=Api GET "/payments/$($pending.Body.id)" $null $residentToken
Check ($pendingDetail.Body.status -eq 'PENDING' -and !$pendingDetail.Body.reversed) 'Rejected reversal attempt leaves pending payment unchanged'

# Approve a separate payment, then exercise guards and audited full reversal.
$paid=Api POST '/payments' @{billId=$paidBill.id;amount=100;mode='CASH';reference="PAYREV-APPROVED-$stamp";paidOn=$day;note='Synthetic reversal test';requestKey=[guid]::NewGuid().ToString()} $paidResidentToken
$approval=Api POST '/payments/approve' @{paymentIds=@($paid.Body.id);verified=$true} $adminToken
if($approval.Status -ne 200){Write-Host "Approval diagnostic: HTTP $($approval.Status) $($approval.Error)"}
Check ($approval.Status -eq 200 -and $approval.Body.approved -eq 1) 'Admin approves the synthetic payment before reversal'
$original=Api GET "/receipts/$($paid.Body.id)" $null $paidResidentToken
$originalNumber=$original.Body.receiptNumber
Check ([bool]$originalNumber -and !$original.Body.reversed) 'Original receipt exists before reversal'
$reportBefore=Api GET "/reports/monthly?month=$month" $null $adminToken
$reason="PAYREV audit correction $stamp"
$key=[guid]::NewGuid().ToString()
$reverseBody=@{verified=$true;reason=$reason;requestKey=$key}
Check ((Api POST "/ops/payments/$($paid.Body.id)/reverse" @{verified=$false;reason=$reason;requestKey=[guid]::NewGuid().ToString()} $treasurerToken).Status -eq 400) 'Treasurer must explicitly verify the financial adjustment'
Check ((Api POST "/ops/payments/$($paid.Body.id)/reverse" @{verified=$true;reason=' ';requestKey=[guid]::NewGuid().ToString()} $treasurerToken).Status -eq 400) 'Reversal requires a non-empty audit reason'
Check ((Api POST "/ops/payments/$($paid.Body.id)/reverse" $reverseBody $residentToken).Status -eq 403) 'Resident cannot reverse a payment'
Check ((Api POST "/ops/payments/$($paid.Body.id)/reverse" $reverseBody $foreignToken).Status -eq 404) 'Foreign tenant cannot reverse a payment by guessed ID'
$reversed=Api POST "/ops/payments/$($paid.Body.id)/reverse" $reverseBody $treasurerToken
Check ($reversed.Status -eq 200 -and $reversed.Body.id -eq $paid.Body.id) 'Treasurer records a full audited reversal'
$retry=Api POST "/ops/payments/$($paid.Body.id)/reverse" $reverseBody $treasurerToken
Check ($retry.Status -eq 200 -and $retry.Body.id -eq $paid.Body.id) 'Same request-key retry is idempotent'
$changed=@{verified=$true;reason="$reason changed";requestKey=$key}
Check ((Api POST "/ops/payments/$($paid.Body.id)/reverse" $changed $treasurerToken).Status -eq 409) 'Reusing the reversal key with different details is rejected'
$duplicate=@{verified=$true;reason=$reason;requestKey=[guid]::NewGuid().ToString()}
Check ((Api POST "/ops/payments/$($paid.Body.id)/reverse" $duplicate $treasurerToken).Status -eq 409) 'A second reversal cannot be recorded with a new key'

# Verify original receipt retention, history synchronization, audit trail and balances.
$payment=Api GET "/payments/$($paid.Body.id)" $null $paidResidentToken
$receipt=Api GET "/receipts/$($paid.Body.id)" $null $paidResidentToken
$history=Api GET '/payments' $null $paidResidentToken
$historyRow=@($history.Body|Where-Object {$_.id -eq $paid.Body.id})[0]
$billAfter=Api GET "/bills/$($paidBill.id)" $null $paidResidentToken
$reportAfter=Api GET "/reports/monthly?month=$month" $null $adminToken
$audit=Api GET '/audit' $null $treasurerToken
$auditRow=@($audit.Body|Where-Object {$_.action -eq 'PAYMENT_REVERSED' -and $_.recordId -eq $paid.Body.id})[0]
$cashbook=Api GET "/ops/cashbook?month=$month" $null $adminToken
$cashRows=@($cashbook.Body|Where-Object {$_.kind -eq 'PAYMENT_REVERSAL' -and $_.title -eq $reason})
if(!$cashRows.Count){Write-Host ('Cashbook diagnostic: '+(@($cashbook.Body|Where-Object {$_.kind -eq 'PAYMENT_REVERSAL'}|Select-Object -Last 3|ConvertTo-Json -Compress -Depth 4)))}
Check ($payment.Body.status -eq 'APPROVED' -and $payment.Body.reversed -and $payment.Body.receiptNumber -eq $originalNumber) 'Original approved payment and receipt remain immutable but marked reversed'
Check ($receipt.Body.receiptNumber -eq $originalNumber -and $receipt.Body.reversed) 'Resident can still view the original receipt with reversal status'
Check ($historyRow.reversed -and $historyRow.status -eq 'APPROVED') 'Resident payment history keeps the original row and marks it reversed'
Check ([decimal]$billAfter.Body.paid -eq 0 -and $billAfter.Body.pendingPaymentId -eq $null) 'Reversed payment no longer counts toward its own bill balance'
Check ([decimal]$reportAfter.Body.refunds -eq [decimal]$reportBefore.Body.refunds+100 -and [decimal]$reportAfter.Body.closing -eq [decimal]$reportBefore.Body.closing-100) 'Monthly report applies the reversal exactly once'
Check ([bool]$auditRow -and $auditRow.afterValue -eq $reason) 'Audit log records the actor action and reason'
Check ($cashRows.Count -eq 1 -and [decimal]$cashRows[0].amount -eq -100) 'Cashbook records the full payment reversal once as a money-out entry'
Write-Host "Completed $script:checks local HTTP/PostgreSQL reversal checks. PAYREV tenants $stamp retained; no records deleted and credentials not printed."
