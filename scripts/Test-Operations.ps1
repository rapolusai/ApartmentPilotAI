# Creates synthetic records only. No DROP, DELETE, TRUNCATE, database reset or production access.
# Executes only against an explicitly local increment-02 backend and retains synthetic records for inspection.
[CmdletBinding()]
param(
 [string]$BaseUrl='http://127.0.0.1:8080/api/v1',
 [switch]$AllowSyntheticRecords,
 [switch]$Concurrency,
 [string]$OutputDirectory=''
)
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'This suite creates test apartments. Re-run with -AllowSyntheticRecords against the LOCAL development database only.'}
$uri=[uri]$BaseUrl
if($uri.Scheme -ne 'http' -or $uri.Host -notin @('localhost','127.0.0.1') -or $uri.UserInfo){throw 'This suite is restricted to your local development backend.'}
if(!$OutputDirectory){$OutputDirectory=Join-Path (Split-Path $PSScriptRoot -Parent) 'test-results'}
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$results=New-Object System.Collections.Generic.List[object]
$stamp=([guid]::NewGuid().ToString()).Substring(0,8)
$script:LastHttp=''
$script:SuiteCompleted=$false
function Check([bool]$ok,[string]$label){
 $results.Add([pscustomobject]@{name=$label;passed=$ok})
 if(!$ok){throw "FAIL: $label. $script:LastHttp"}
 Write-Host "PASS: $label"
}
function Key { return [guid]::NewGuid().ToString() }
function Mobile { return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString() }
function Api([string]$method,[string]$path,[object]$body=$null,[string]$token=''){
 $headers=@{};if($token){$headers.Authorization="Bearer $token"}
 $args=@{Uri="$BaseUrl$path";Method=$method;Headers=$headers;ContentType='application/json';TimeoutSec=30}
 if($null -ne $body){$args.Body=[Text.Encoding]::UTF8.GetBytes(($body|ConvertTo-Json -Depth 20 -Compress))}
 try{$value=Invoke-RestMethod @args;$script:LastHttp="$method $path -> 200";return [pscustomobject]@{Status=200;Body=$value}}
 catch{
  if(!$_.Exception.Response){throw}
  $status=[int]$_.Exception.Response.StatusCode
  $script:LastHttp="$method $path -> HTTP $status"
  return [pscustomobject]@{Status=$status;Body=$null}
 }
}
function PostCommand([string]$path,[hashtable]$body,[string]$token){if(!$body.ContainsKey('requestKey')){$body.requestKey=Key};return Api 'POST' $path $body $token}
function SettingsPayload([object]$s,[bool]$visible){
 return @{
  payee=[string]$s.payee;upi=[string]$s.upi;bank=[string]$s.bank;account=[string]$s.account;ifsc=[string]$s.ifsc
  billVacant=[bool]$s.billVacant;lateEnabled=[bool]$s.lateEnabled;lateFee=$s.lateFee;graceDays=[int]$s.graceDays
  dueNotify=[bool]$s.dueNotify;reminders=[bool]$s.reminders;reminderDays=[string]$s.reminderDays
  expensesVisible=$visible;proofRequired=[bool]$s.proofRequired;quietStart=[string]$s.quietStart;quietEnd=[string]$s.quietEnd
  bookingRules=[string]$s.bookingRules
 }
}
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$month=Get-Date -Format 'yyyy-MM';$date=Get-Date -Format 'yyyy-MM-dd'
$start=[DateTime]::UtcNow.Date.AddDays(2).AddHours(10).ToString("yyyy-MM-dd'T'HH:mm:ss'Z'")
$end=[DateTime]::UtcNow.Date.AddDays(2).AddHours(12).ToString("yyyy-MM-dd'T'HH:mm:ss'Z'")
try {
 $status=Api 'GET' '/status';Check ($status.Status -eq 200 -and $status.Body.version -eq '0.2.0' -and $status.Body.localRegistration) 'Increment 02 local backend is running'
 $a=Api 'POST' '/auth/register' @{name='TEST02 Admin A';mobile=(Mobile);pin=$pin;apartmentName="TEST02 A $stamp";city='Hyderabad';flats=5}
 Check ($a.Status -eq 200) 'Create synthetic apartment A';$ta=$a.Body.token
 $b=Api 'POST' '/auth/register' @{name='TEST02 Admin B';mobile=(Mobile);pin=$pin;apartmentName="TEST02 B $stamp";city='Bengaluru';flats=5}
 Check ($b.Status -eq 200) 'Create independent apartment B';$tb=$b.Body.token
 $invite=Api 'POST' '/invites' @{} $ta;Check ($invite.Status -eq 200) 'Create resident invite'
 $rm=Mobile
 $joined=Api 'POST' '/auth/join' @{invite=$invite.Body.code;flatLabel='A-101';name='TEST02 Resident';mobile=$rm;pin=$pin}
 Check ($joined.Body.status -eq 'PENDING') 'Resident signup does not grant access'
 $all=Api 'GET' '/members' $null $ta;$member=@($all.Body|Where-Object {$_.mobile -eq $rm})[0]
 $approve=Api 'POST' "/members/$($member.id)/approve" @{} $ta;Check ($approve.Status -eq 200) 'Admin verifies resident'
 $login=Api 'POST' '/auth/login' @{mobile=$rm;pin=$pin;role='RESIDENT'};Check ($login.Status -eq 200) 'Resident signs in';$tr=$login.Body.token
 $flats=Api 'GET' '/ops/flats' $null $ta;$flat=@($flats.Body|Where-Object {$_.label -eq 'A-101'})[0]
 $own=Api 'GET' '/ops/flats' $null $tr;Check (@($own.Body).Count -eq 1) 'Resident flat selector is limited to own flat'
 $directory=PostCommand '/ops/directory' @{name='TEST02 Directory only';flatId=$flat.id;mobile='';residentType='OWNER'} $ta
 Check ($directory.Status -eq 200) 'Manual member record does not require a login identity'
 $committee=PostCommand '/ops/committee' @{name='TEST02 President';title='President';flatId=$flat.id;startOn=$date;public=$true;active=$true} $ta
 Check ($committee.Status -eq 200) 'Committee title recorded without granting Admin access'
 $denied=PostCommand '/ops/committee' @{name='TEST02 Forbidden';title='President';startOn=$date} $tr;Check ($denied.Status -eq 403) 'Resident cannot appoint committee members'
 $vendor=PostCommand '/ops/contacts' @{kind='VENDOR';name='TEST02 Lift service';category='Lift';phone='';notes='Synthetic';public=$true;active=$true} $ta
 Check ($vendor.Status -eq 200) 'Vendor created'
 $ticket=PostCommand '/ops/tickets' @{kind='ISSUE';title='TEST02 Lift stopped';description='Synthetic incident';category='Lift';scope='ALL';priority='HIGH'} $tr
 Check ($ticket.Status -eq 200) 'Resident reports building incident';$tid=$ticket.Body.id
 $forbidden=Api 'GET' "/ops/tickets/$tid" $null $tb;Check ($forbidden.Status -eq 404) 'Cross-apartment ticket denied'
 $selfResolve=PostCommand "/ops/tickets/$tid/status" @{status='RESOLVED';note='Attempt'} $tr;Check ($selfResolve.Status -eq 400) 'Resident cannot perform staff resolution'
 $assigned=PostCommand "/ops/tickets/$tid/status" @{status='ASSIGNED';note='Technician assigned';vendorId=$vendor.Body.id} $ta;Check ($assigned.Status -eq 200) 'Admin assigns vendor'
 $resolved=PostCommand "/ops/tickets/$tid/status" @{status='RESOLVED';note='Technician completed work';vendorId=$vendor.Body.id} $ta;Check ($resolved.Status -eq 200) 'Admin resolves issue'
 $closed=PostCommand "/ops/tickets/$tid/status" @{status='CLOSED';note='Resident confirms restoration'} $tr;Check ($closed.Status -eq 200) 'Reporter confirms closure'
 $complaint=PostCommand '/ops/tickets' @{kind='COMPLAINT';title='TEST02 Private leakage';description='Flat-specific request';category='Plumbing';priority='NORMAL'} $tr
 Check ($complaint.Status -eq 200) 'Private flat complaint created'
 $rule=Api 'POST' '/billing/rules' @{amount=1500;effective=$month;billingDay=1;dueDay=10} $ta;Check ($rule.Status -eq 200) 'Maintenance amount is configurable'
 $generated=Api 'POST' "/billing/generate?month=$month" @{} $ta;Check ($generated.Status -eq 200) 'Generate maintenance'
 $billList=Api 'GET' "/bills?month=$month" $null $tr;$bill=@($billList.Body)[0]
 Check ($bill.amount -eq 1500) 'Bill uses configured amount rather than hardcoded sample'
 $chargeBody=@{kind='CONTRIBUTION';title='TEST02 Lift contribution';amount=250;month=$month;dueDate=$date;flatId=$flat.id;requestKey=(Key)}
 $charge=Api 'POST' '/ops/charges' $chargeBody $ta;Check ($charge.Body.created -eq 1) 'One-time contribution created separately'
 $chargeAgain=Api 'POST' '/ops/charges' $chargeBody $ta;Check ($chargeAgain.Body.id -eq $charge.Body.id) 'Contribution retry does not duplicate charges'
 $previewDenied=Api 'POST' '/ops/charges/preview' @{kind='CONTRIBUTION';title='Forbidden preview';amount=250;month=$month;dueDate=$date;scope='ALL'} $tr
 Check ($previewDenied.Status -eq 403) 'Resident cannot preview apartment-wide contributions'
 $past=(Get-Date).AddDays(-1).ToString('yyyy-MM-dd')
 $pastPreview=Api 'POST' '/ops/charges/preview' @{kind='CONTRIBUTION';title='Past contribution';amount=250;month=$month;dueDate=$past;scope='ALL'} $ta
 Check ($pastPreview.Status -eq 400) 'Past-due contribution preview is rejected'
 $invalidPreview=Api 'POST' '/ops/charges/preview' @{kind='CONTRIBUTION';title='Invalid flats';amount=250;month=$month;dueDate=$date;scope='SELECTED';flatLabels='Z-999'} $ta
 Check ($invalidPreview.Status -eq 400) 'Contribution preview rejects unknown flat labels'
 $crossPreview=Api 'POST' '/ops/charges/preview' @{kind='CONTRIBUTION';title='Foreign flat';amount=250;month=$month;dueDate=$date;flatId=$flat.id} $tb
 Check ($crossPreview.Status -eq 400) 'Contribution preview parent-checks a supplied flat ID'
 $multiPreview=Api 'POST' '/ops/charges/preview' @{kind='CONTRIBUTION';title='TEST02 Generator contribution';amount=250;month=$month;dueDate=$date;scope='SELECTED';flatLabels='A-101, A-102'} $ta
 Check ($multiPreview.Status -eq 200 -and $multiPreview.Body.flatCount -eq 2 -and $multiPreview.Body.total -eq 500 -and $multiPreview.Body.flatLabels -eq 'A-101, A-102') 'Server previews selected-flat contribution total'
 $multiBody=@{kind=$multiPreview.Body.kind;title=$multiPreview.Body.title;amount=$multiPreview.Body.amount;month=$multiPreview.Body.month;dueDate=$multiPreview.Body.dueDate;flatLabels=$multiPreview.Body.flatLabels;requestKey=(Key)}
 $multiCharge=Api 'POST' '/ops/charges' $multiBody $ta;Check ($multiCharge.Status -eq 200 -and $multiCharge.Body.created -eq 2) 'Create contribution from frozen server preview'
 $multiAgain=Api 'POST' '/ops/charges' $multiBody $ta;Check ($multiAgain.Body.id -eq $multiCharge.Body.id) 'Selected-flat contribution retry is idempotent'
 $billList=Api 'GET' "/bills?month=$month" $null $tr;Check (@($billList.Body).Count -eq 3) 'Contributions remain separate from monthly bill'
 $expenseBody=@{title='TEST02 Water';category='Water';amount=100;paidOn=$date;paid=$false;visibleToResidents=$true;mode='UPI';notes='Synthetic';requestKey=(Key)}
 $expense=Api 'POST' '/ops/expenses' $expenseBody $ta;Check ($expense.Status -eq 200) 'Create expense draft with payment mode'
 $draftResident=Api 'GET' "/ops/expenses/$($expense.Body.id)" $null $tr;Check ($draftResident.Status -eq 403) 'Unpaid expense draft hidden from resident'
 $expenseBody.requestKey=Key;$expenseBody.paid=$true
 $confirm=Api 'POST' "/ops/expenses/$($expense.Body.id)" $expenseBody $ta;Check ($confirm.Status -eq 200) 'Confirm draft paid'
 $detail=Api 'GET' "/ops/expenses/$($expense.Body.id)" $null $ta;Check ($detail.Body.mode -eq 'UPI' -and $detail.Body.paid) 'Expense stores UPI mode accurately'
 $before=Api 'GET' "/reports/monthly?month=$month" $null $ta
 $reverseBody=@{verified=$true;reason='Synthetic correction';requestKey=(Key)}
 $reversal=Api 'POST' "/ops/expenses/$($expense.Body.id)/reverse" $reverseBody $ta;Check ($reversal.Status -eq 200) 'Reverse expense without deleting the original'
 $reversalAgain=Api 'POST' "/ops/expenses/$($expense.Body.id)/reverse" $reverseBody $ta;Check ($reversalAgain.Status -eq 200) 'Reversal retry is idempotent'
 $after=Api 'GET' "/reports/monthly?month=$month" $null $ta;Check (($after.Body.balance-$before.Body.balance) -eq 100) 'Financial report includes expense reversal once'
 $residentReport=Api 'GET' "/reports/monthly?month=$month" $null $tr;Check ($residentReport.Status -eq 200) 'Resident sees transparency totals while sharing is enabled'
 $settings=Api 'GET' '/ops/settings' $null $ta;Check ($settings.Status -eq 200) 'Admin loads authoritative transparency settings'
 $privacyOff=Api 'POST' '/ops/settings' (SettingsPayload $settings.Body $false) $ta;Check ($privacyOff.Status -eq 200) 'Admin disables resident expense transparency'
 $privateReport=Api 'GET' "/reports/monthly?month=$month" $null $tr;Check ($privateReport.Status -eq 403) 'Resident totals are denied when transparency is disabled'
 $staffReport=Api 'GET' "/reports/monthly?month=$month" $null $ta;Check ($staffReport.Status -eq 200) 'Transparency setting does not hide staff financial totals'
 $privacyOn=Api 'POST' '/ops/settings' (SettingsPayload $settings.Body $true) $ta;Check ($privacyOn.Status -eq 200) 'Admin restores resident expense transparency'
 $restoredReport=Api 'GET' "/reports/monthly?month=$month" $null $tr;Check ($restoredReport.Status -eq 200) 'Resident totals return after transparency is restored'
 $notice=PostCommand '/notices' @{title='TEST02 Selected notice';body='Synthetic notification';audience='SELECTED';audienceValue='A-101';publish=$true;acknowledge=$true} $ta
 Check ($notice.Status -eq 200) 'Publish targeted notice';$nid=$notice.Body.id
 $nRead=Api 'GET' "/notices/$nid" $null $tr;Check ($nRead.Status -eq 200) 'Selected resident sees notice'
 $nForeign=Api 'GET' "/notices/$nid" $null $tb;Check ($nForeign.Status -eq 404) 'Another apartment cannot open notice'
 $ack=Api 'POST' "/notices/$nid/read" @{acknowledge=$true} $tr;Check ($ack.Status -eq 200) 'Resident acknowledges notice'
 $track=Api 'GET' "/notices/$nid" $null $ta;Check (@($track.Body.recipients|Where-Object {$_.acknowledgedAt}).Count -eq 1) 'Admin acknowledgement tracking'
 $doc=PostCommand '/ops/documents' @{title='TEST02 Confidential';category='AMC';public=$false} $ta;Check ($doc.Status -eq 200) 'Private document metadata created'
 $docDenied=Api 'GET' "/ops/documents/$($doc.Body.id)" $null $tr;Check ($docDenied.Status -eq 403) 'Private document denied to residents'
 $fileBody=@{kind='DOCUMENT';parentId=$doc.Body.id;name='synthetic.pdf';content=[Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("%PDF-1.4`n% Synthetic signature-only test content. Not a readable report.`n"))}
 $file=Api 'POST' '/ops/files' $fileBody $ta;Check ($file.Status -eq 200) 'Attachment contents stored with parent access control'
 $fileAgain=Api 'POST' '/ops/files' $fileBody $ta;Check ($fileAgain.Body.id -eq $file.Body.id) 'Identical file retry is deduplicated'
 $noFile=Api 'GET' "/ops/files/$($file.Body.id)" $null $tr;Check ($noFile.Status -eq 403) 'Private attachment cannot be fetched by ID'
 $resource=PostCommand '/ops/resources' @{name='TEST02 Guest car bay';kind='CAR';capacity=1;bookable=$true;conflicts=@()} $ta
 Check ($resource.Status -eq 200) 'Configure guest parking resource';$rid=$resource.Body.id
 $bookingBody=@{title='TEST02 Birthday';eventType='BIRTHDAY';guests=10;start=$start;end=$end;items=@(@{resourceId=$rid;quantity=1});acceptRules=$true;requestKey=(Key)}
 $booking=Api 'POST' '/ops/bookings' $bookingBody $tr;Check ($booking.Body.status -eq 'PENDING') 'Booking request is not a confirmed reservation';$bid=$booking.Body.id
 $bookingBody.requestKey=Key;$bookingBody.title='TEST02 Competing request'
 $second=Api 'POST' '/ops/bookings' $bookingBody $tr;Check ($second.Status -eq 200) 'Pending requests do not lock availability'
 $approved=PostCommand "/ops/bookings/$bid/decision" @{action='APPROVE';revision=1;note='Approved'} $ta;Check ($approved.Body.status -eq 'APPROVED') 'Admin reserves capacity atomically'
 $conflict=PostCommand "/ops/bookings/$($second.Body.id)/decision" @{action='APPROVE';revision=1;note='Conflicting attempt'} $ta;Check ($conflict.Status -eq 409) 'Second approval cannot double-book parking'
 $cancel=PostCommand "/ops/bookings/$bid/decision" @{action='CANCEL';revision=2;note='Cancelled by organiser'} $tr;Check ($cancel.Body.status -eq 'CANCELLED') 'Resident cancellation releases parking'
 $approveSecond=PostCommand "/ops/bookings/$($second.Body.id)/decision" @{action='APPROVE';revision=1;note='Now available'} $ta;Check ($approveSecond.Body.status -eq 'APPROVED') 'Released capacity becomes available'
 $poll=PostCommand '/ops/polls' @{title='TEST02 Meeting';description='Choose time';closesAt=$start;options=@('Morning','Evening')} $ta;Check ($poll.Status -eq 200) 'Create community poll';$pollId=$poll.Body.id
 $p=Api 'GET' "/ops/polls/$pollId" $null $tr;$options=@($p.Body.options)
 $vote=Api 'POST' "/ops/polls/$pollId/vote" @{optionId=$options[0].id} $tr;Check ($vote.Status -eq 200) 'Flat votes once'
 $voteAgain=Api 'POST' "/ops/polls/$pollId/vote" @{optionId=$options[1].id} $tr;Check ($voteAgain.Status -eq 200) 'Flat can update vote while open'
 $p=Api 'GET' "/ops/polls/$pollId" $null $ta;Check (($p.Body.options|Measure-Object -Property votes -Sum).Sum -eq 1) 'Changing option does not create extra votes'
 $vehicle=PostCommand '/ops/vehicles' @{registration=('TEST'+$stamp.ToUpper());kind='CAR';parkingLabel='P1'} $tr;Check ($vehicle.Status -eq 200) 'Resident registers own flat vehicle'
 $sub=Api 'GET' '/ops/subscription' $null $ta;Check ($sub.Body.price -eq 99 -and !$sub.Body.checkoutConfigured) 'Subscription price is distinct from maintenance; live checkout not falsely enabled'
 $noProvider=Api 'POST' "/provider/apartments/$($a.Body.account.tenantId)/verify" @{verifiedBy='test';identityAndApartmentVerified=$true} $ta;Check ($noProvider.Status -eq 403) 'Apartment Admin cannot self-approve provider verification'
 $selfReferral=Api 'POST' '/ops/referrals/claim' @{code=$sub.Body.referralCode} $ta;Check ($selfReferral.Status -eq 409) 'Self-referral rejected'
 if($Concurrency){
  $newStart=[DateTime]::UtcNow.Date.AddDays(3).AddHours(10).ToString("yyyy-MM-dd'T'HH:mm:ss'Z'")
  $newEnd=[DateTime]::UtcNow.Date.AddDays(3).AddHours(12).ToString("yyyy-MM-dd'T'HH:mm:ss'Z'")
  $ids=@()
  foreach($n in 1,2){$q=PostCommand '/ops/bookings' @{title="TEST02 Parallel $n";eventType='OTHER';guests=5;start=$newStart;end=$newEnd;items=@(@{resourceId=$rid;quantity=1});acceptRules=$true} $tr;Check ($q.Status -eq 200) "Parallel request $n created";$ids+=@($q.Body.id)}
  $jobs=@();foreach($record in $ids){$body=@{action='APPROVE';revision=1;note='Parallel approval check';requestKey=(Key)}|ConvertTo-Json -Compress
   $jobs+=Start-Job -ArgumentList "$BaseUrl/ops/bookings/$record/decision",$ta,$body -ScriptBlock {param($url,$token,$body) try{Invoke-RestMethod -Method POST -Uri $url -Headers @{Authorization="Bearer $token"} -ContentType 'application/json' -Body $body -TimeoutSec 30|Out-Null;200}catch{if($_.Exception.Response){[int]$_.Exception.Response.StatusCode}else{0}}}
  }
  try{$jobs|Wait-Job -Timeout 45|Out-Null;$codes=@($jobs|Receive-Job);Check ((@($codes|Where-Object {$_ -eq 200}).Count -eq 1) -and (@($codes|Where-Object {$_ -eq 409}).Count -eq 1)) 'Concurrent approvals permit exactly one reservation'}finally{$jobs|Stop-Job -ErrorAction SilentlyContinue;$jobs|Remove-Job -Force -ErrorAction SilentlyContinue}
 }
 $script:SuiteCompleted=$true
 Write-Host "Completed $($results.Count) checks. Synthetic tenants TEST02 A/B $stamp remain for inspection. No data was deleted."
} finally {
 $file=Join-Path $OutputDirectory ("operations-$stamp.json")
 @{run=$stamp;time=(Get-Date).ToString('o');scope='Local HTTP integration';checks=[object[]]$results;complete=($script:SuiteCompleted -and $results.Count -gt 0 -and @($results|Where-Object {!$_.passed}).Count -eq 0)}|ConvertTo-Json -Depth 15|Set-Content -Path $file -Encoding UTF8
 Write-Host "Evidence: $file (no passwords or bearer tokens recorded)"
}
