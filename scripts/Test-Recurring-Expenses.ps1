[CmdletBinding()]
param([string]$BaseUrl='http://127.0.0.1:8081/api/v1',[switch]$AllowSyntheticRecords)
$ErrorActionPreference='Stop'
if(-not $AllowSyntheticRecords){throw 'This suite creates labelled synthetic apartments; explicitly enable local testing.'}
if(([uri]$BaseUrl).Host -notin @('127.0.0.1','localhost')){throw 'Local backend only.'}
$script:checks=0
function Api([string]$method,[string]$path,$body=$null,[string]$token='') {
 $headers=@{};if($token){$headers.Authorization="Bearer $token"}
 $args=@{Method=$method;Uri="$BaseUrl$path";Headers=$headers;ContentType='application/json';TimeoutSec=25}
 if($null -ne $body){$args.Body=$body|ConvertTo-Json -Depth 8 -Compress}
 try{[pscustomobject]@{Status=200;Body=(Invoke-RestMethod @args)}}
 catch{if(-not $_.Exception.Response){throw};[pscustomobject]@{Status=[int]$_.Exception.Response.StatusCode;Body=$null}}
}
function Check([bool]$condition,[string]$name){if(-not $condition){throw "FAIL: $name"};$script:checks++;Write-Host "PASS: $name"}
function Key{return [guid]::NewGuid().ToString()}
function Mobile{return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999)}
function Command([string]$path,[hashtable]$body,[string]$token){$body.requestKey=Key;Api POST $path $body $token}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$status=Api GET '/status';Check ($status.Body.localRegistration -eq $true) 'Explicit local registration enabled'
$a=Api POST '/auth/register' @{name='RECURRING QA Admin A';mobile=(Mobile);pin=$pin;apartmentName="RECURRING QA A $stamp";city='Hyderabad';flats=5}
$b=Api POST '/auth/register' @{name='RECURRING QA Admin B';mobile=(Mobile);pin=$pin;apartmentName="RECURRING QA B $stamp";city='Pune';flats=5}
Check ($a.Status -eq 200 -and $b.Status -eq 200) 'Two isolated synthetic apartments created'
$ta=$a.Body.token;$tb=$b.Body.token
$mobile=Mobile
$invite=Command '/invites' @{} $ta
$join=Api POST '/auth/join' @{name='RECURRING QA Resident';mobile=$mobile;pin=$pin;invite=$invite.Body.code;flatLabel='A-101';residentType='OWNER'}
Check ($join.Body.status -eq 'PENDING') 'Resident begins pending'
$members=Api GET '/members' $null $ta;$member=@($members.Body|Where-Object mobile -eq $mobile)[0]
Check ((Api POST "/members/$($member.id)/approve" @{verified=$true} $ta).Status -eq 200) 'Resident approved'
$login=Api POST '/auth/login' @{mobile=$mobile;pin=$pin;role='RESIDENT'};Check ($login.Status -eq 200) 'Resident signs in';$tr=$login.Body.token
$vendor=Command '/ops/contacts' @{kind='VENDOR';name='RECURRING QA Security';category='Security';phone='';notes='Synthetic';public=$true;active=$true} $ta
$foreignVendor=Command '/ops/contacts' @{kind='VENDOR';name='RECURRING QA Foreign';category='Security';phone='';notes='Synthetic';public=$true;active=$true} $tb
Check ($vendor.Status -eq 200 -and $foreignVendor.Status -eq 200) 'Tenant-scoped vendors created'
$cycle=(Get-Date).AddDays(10).ToString('yyyy-MM-dd')
$template=@{title='RECURRING QA Security';category='Security';amount=100;frequency='MONTHLY';nextOn=$cycle;active=$true;vendorId=$vendor.Body.id;requestKey=(Key)}
Check ((Api POST '/ops/recurring' $template $tr).Status -eq 403) 'Resident cannot create recurring templates'
$bad=$template.Clone();$bad.requestKey=Key;$bad.vendorId=$foreignVendor.Body.id
Check ((Api POST '/ops/recurring' $bad $ta).Status -eq 404) 'Foreign vendor cannot be linked'
$created=Api POST '/ops/recurring' $template $ta;Check ($created.Status -eq 200) 'Recurring template persists';$id=$created.Body.id
Check ((Api POST '/ops/recurring' $template $ta).Body.id -eq $id) 'Template creation retry is idempotent'
$detail=Api GET "/ops/recurring/$id" $null $ta
Check ($detail.Body.vendorName -eq 'RECURRING QA Security' -and $detail.Body.vendorId -eq $vendor.Body.id) 'Detail resolves the authenticated tenant vendor'
Check ((Api GET "/ops/recurring/$id").Status -eq 401) 'Anonymous detail denied'
Check ((Api GET "/ops/recurring/$id" $null $tr).Status -eq 403) 'Resident template detail denied'
Check ((Api GET "/ops/recurring/$id" $null $tb).Status -eq 404) 'Foreign template detail denied'
$legacy=$template.Clone();$legacy.Remove('vendorId');$legacy.requestKey=Key
Check ((Api POST "/ops/recurring/$id" $legacy $ta).Status -eq 200) 'Older form without vendor field can still save'
Check ((Api GET "/ops/recurring/$id" $null $ta).Body.vendorId -eq $vendor.Body.id) 'Omitted vendor field preserves existing association'
$prepare=@{cycle=$cycle;requestKey=(Key)}
Check ((Api POST "/ops/recurring/$id/prepare" $prepare $tr).Status -eq 403) 'Resident cannot prepare an expense'
Check ((Api POST "/ops/recurring/$id/prepare" $prepare $tb).Status -eq 404) 'Foreign tenant cannot prepare a guessed template'
Check ((Command "/ops/recurring/$id/prepare" @{cycle=$cycle;tenantId=$a.Body.account.tenantId} $ta).Status -eq 400) 'Client tenant override rejected'
Check ((Command "/ops/recurring/$id/prepare" @{cycle='invalid'} $ta).Status -eq 400) 'Invalid cycle rejected'
Check ((Command "/ops/recurring/$id/prepare" @{cycle=(Get-Date).ToString('yyyy-MM-dd')} $ta).Status -eq 409) 'Stale cycle rejected'
$draft=Api POST "/ops/recurring/$id/prepare" $prepare $ta
Check ($draft.Status -eq 200) 'Preparing a future cycle creates a real unpaid draft';$expense=$draft.Body.id
$expenseDetail=Api GET "/ops/expenses/$expense" $null $ta
Check ($expenseDetail.Body.paid -eq $false -and $expenseDetail.Body.amount -eq 100 -and $expenseDetail.Body.recurringOn -eq $cycle -and $expenseDetail.Body.vendorId -eq $vendor.Body.id) 'Draft snapshots amount, cycle and vendor without posting cash'
Check ((Api GET "/ops/expenses/$expense" $null $tr).Status -eq 403) 'Unpaid draft remains hidden from residents'
Check ((Api GET "/ops/recurring/$id" $null $ta).Body.nextOn -eq $cycle) 'Preparation does not advance the manual cycle'
Check ((Api POST "/ops/recurring/$id/prepare" $prepare $ta).Body.id -eq $expense) 'Same request key returns the original draft'
Check ((Command "/ops/recurring/$id/prepare" @{cycle=$cycle} $ta).Body.id -eq $expense) 'Another request key still returns the same cycle draft'
$jobs=@()
foreach($n in 1,2){
 $body=@{cycle=$cycle;requestKey=(Key)}|ConvertTo-Json -Compress
 $jobs+=Start-Job -ArgumentList "$BaseUrl/ops/recurring/$id/prepare",$ta,$body -ScriptBlock {param($url,$token,$body)try{$r=Invoke-RestMethod -Method POST -Uri $url -Headers @{Authorization="Bearer $token"} -ContentType application/json -Body $body -TimeoutSec 25;[pscustomobject]@{status=200;id=$r.id}}catch{[pscustomobject]@{status=[int]$_.Exception.Response.StatusCode;id=''}}}
}
try{$jobs|Wait-Job -Timeout 45|Out-Null;$parallel=@($jobs|Receive-Job);Check ($parallel.Count -eq 2 -and @($parallel|Where-Object {$_.status -ne 200 -or $_.id -ne $expense}).Count -eq 0) 'Concurrent preparations return one existing cycle draft'}finally{$jobs|Stop-Job -ErrorAction SilentlyContinue;$jobs|Remove-Job -Force -ErrorAction SilentlyContinue}
$pause=@{active=$false;expectedActive=$true;requestKey=(Key)}
Check ((Api POST "/ops/recurring/$id/active" $pause $tr).Status -eq 403) 'Resident cannot pause template'
Check ((Api POST "/ops/recurring/$id/active" $pause $ta).Status -eq 200) 'Pause persists'
Check ((Api POST "/ops/recurring/$id/active" $pause $ta).Status -eq 200) 'Pause retry is idempotent'
Check ((Api GET "/ops/recurring/$id" $null $ta).Body.active -eq $false) 'Paused state survives revisit'
Check ((Command "/ops/recurring/$id/prepare" @{cycle=$cycle} $ta).Status -eq 409) 'Paused template cannot prepare with a new request'
Check ((Api POST "/ops/recurring/$id/prepare" $prepare $ta).Body.id -eq $expense) 'Original prepare retry survives a later pause'
Check ((Command "/ops/recurring/$id/active" @{active=$false;expectedActive=$true} $ta).Status -eq 409) 'Stale active-state decision rejected'
Check ((Command "/ops/recurring/$id/active" @{active='true';expectedActive=$false} $ta).Status -eq 400) 'Non-boolean active state rejected'
Check ((Command "/ops/recurring/$id/active" @{active=$true;expectedActive=$false} $ta).Status -eq 200) 'Resume persists'
$other=$template.Clone();$other.title='RECURRING QA Other';$other.requestKey=Key
$otherId=(Api POST '/ops/recurring' $other $ta).Body.id
Check ((Api POST "/ops/recurring/$otherId/prepare" $prepare $ta).Status -eq 409) 'Prepare key cannot be replayed against a different template'
Check ((Api POST "/ops/recurring/$otherId" $template $ta).Status -eq 409) 'Template key cannot silently target a different record'
$edit=@{title='RECURRING QA Security';category='Security';amount=100;paidOn=(Get-Date).ToString('yyyy-MM-dd');mode='BANK_TRANSFER';notes='Synthetic confirmed payment';paid=$true;visibleToResidents=$true;requestKey=(Key)}
Check ((Api POST "/ops/expenses/$expense" $edit $ta).Status -eq 200) 'Explicit paid confirmation posts the prepared expense'
$next=([datetime]$cycle).AddMonths(1).ToString('yyyy-MM-dd')
Check ((Api GET "/ops/recurring/$id" $null $ta).Body.nextOn -eq $next) 'Paid confirmation advances exactly one manual cycle'
Check ((Api POST "/ops/expenses/$expense" $edit $ta).Status -eq 200) 'Paid confirmation retry succeeds without reposting'
Check ((Api GET "/ops/recurring/$id" $null $ta).Body.nextOn -eq $next) 'Confirmation retry does not advance again'
Check ((Api GET "/ops/expenses/$expense" $null $tr).Body.paid -eq $true) 'Resident sees only the confirmed public expense'
Check ((Api POST "/ops/recurring/$id/prepare" $prepare $ta).Body.id -eq $expense) 'Prepare retry after payment returns the original record'
Check ((Command "/ops/recurring/$id/prepare" @{cycle=$cycle} $ta).Status -eq 409) 'New command cannot prepare an already advanced cycle'
Check ((Api GET "/ops/expenses/$expense" $null $tb).Status -eq 404) 'Foreign tenant cannot open the prepared expense'
$report=Api GET "/reports/monthly?month=$((Get-Date).ToString('yyyy-MM'))" $null $ta
Check ($report.Body.spent -eq 100) 'Monthly cash movement counts the confirmed expense once'
Check ((Command "/ops/expenses/$expense/reverse" @{reason='Synthetic regression correction';verified=$true} $ta).Status -eq 200) 'Paid record uses audited reversal instead of deletion'
Check (-not [string]::IsNullOrWhiteSpace((Api GET "/ops/expenses/$expense" $null $ta).Body.reversedOn)) 'Reversed original remains accessible'
$scheduled=$template.Clone();$scheduled.title='RECURRING QA Scheduled';$scheduled.nextOn=(Get-Date).ToString('yyyy-MM-dd');$scheduled.requestKey=Key
$scheduledId=(Api POST '/ops/recurring' $scheduled $ta).Body.id
$scheduledDraft=Command "/ops/recurring/$scheduledId/prepare" @{cycle=$scheduled.nextOn} $ta
Check ($scheduledDraft.Status -eq 200) 'Current scheduled cycle can be prepared manually'
Check ((Api POST '/ops/automation/run' @{} $ta).Status -eq 200) 'Existing automation processes due cycles with vendor columns'
$expenses=Api GET "/expenses?month=$((Get-Date).ToString('yyyy-MM'))" $null $ta
$scheduledRows=@($expenses.Body|Where-Object title -eq $scheduled.title)
Check ($scheduledRows.Count -eq 1 -and $scheduledRows[0].paid -eq $false) 'Automation retains the manual cycle draft without posting payment'
$scheduledDetail=Api GET "/ops/expenses/$($scheduledDraft.Body.id)" $null $ta
Check ($scheduledDetail.Body.vendorId -eq $vendor.Body.id) 'Scheduled processing preserves the snapshotted vendor'
$scheduledNext=([datetime]$scheduled.nextOn).AddMonths(1).ToString('yyyy-MM-dd')
Check ((Api GET "/ops/recurring/$scheduledId" $null $ta).Body.nextOn -eq $scheduledNext) 'Automation preserves existing catch-up advancement'
$scheduledEdit=$edit.Clone();$scheduledEdit.title=$scheduled.title;$scheduledEdit.requestKey=Key
Check ((Api POST "/ops/expenses/$($scheduledDraft.Body.id)" $scheduledEdit $ta).Status -eq 200) 'Scheduled draft can be confirmed separately'
Check ((Api GET "/ops/recurring/$scheduledId" $null $ta).Body.nextOn -eq $scheduledNext) 'Posting an automated cycle does not advance twice'
$auto=$scheduled.Clone();$auto.title='RECURRING QA Automatic';$auto.requestKey=Key
$autoId=(Api POST '/ops/recurring' $auto $ta).Body.id
Check ((Api POST '/ops/automation/run' @{} $ta).Status -eq 200) 'Automation creates a new due template draft'
$autoRows=@((Api GET "/expenses?month=$((Get-Date).ToString('yyyy-MM'))" $null $ta).Body|Where-Object title -eq $auto.title)
Check ($autoRows.Count -eq 1 -and $autoRows[0].paid -eq $false) 'New automated cycle has one unpaid draft'
$autoDetail=Api GET "/ops/expenses/$($autoRows[0].id)" $null $ta
Check ($autoDetail.Body.vendorId -eq $vendor.Body.id) 'Automated draft copies the real tenant vendor'
Check ((Api GET "/ops/recurring/$autoId" $null $ta).Body.nextOn -eq $scheduledNext) 'Automated draft advances the existing schedule once'
Write-Host "Completed $script:checks recurring checks. Synthetic RECURRING QA A/B $stamp retained; no data deleted."
