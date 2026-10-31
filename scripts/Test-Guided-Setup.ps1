# Guided setup checks over HTTP: the setup lock, step order, server-side validation, the flat generator save,
# resident and treasurer wizards and the Admin readiness checklist. Creates synthetic local records only.
# Run against a backend started with the setup lock ON (the default; APP_SETUP_ENFORCED unset or true).
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
function Key { return [guid]::NewGuid().ToString() }
function Done([string]$step,[string]$token){ return Api 'POST' "/setup/steps/$step/complete" @{} $token }
$stamp=([guid]::NewGuid().ToString()).Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$mobileA=Mobile;$mobileR=Mobile
$a=Api 'POST' '/auth/register' @{name='TEST Setup Admin';mobile=$mobileA;pin=$pin;apartmentName="TEST Setup $stamp";city='Hyderabad';flats=10}
Check ($a.Status -eq 200) 'Create apartment';$t=$a.Body.token
$s=Api 'GET' '/setup' $null $t;Check ($s.Body.current -eq 'apartment' -and $s.Body.total -eq 9) 'New Admin starts at step 1 of 9'
Check ((Api 'GET' '/members' $null $t).Status -eq 428) 'Other features are locked until setup is done (HTTP 428)'
Check ((Done 'payment' $t).Status -eq 409) 'Steps cannot be skipped'
Check ((Done 'apartment' $t).Status -eq 400) 'Apartment step needs an address and PIN code'
Check ((Api 'POST' '/ops/apartment' @{name="TEST Setup $stamp";city='Hyderabad';address='Road 1';pincode='500081'} $t).Status -eq 200) 'Save apartment details'
Check ((Done 'apartment' $t).Body.current -eq 'blocks') 'Step 1 done, step 2 next'
Check ((Done 'blocks' $t).Body.current -eq 'flats') 'Blocks step done'
$flats=@();foreach($f in 1..10){foreach($n in 1..10){$flats+=@{label=('T1-{0}' -f ($f*100+$n));block='T1';floor=$f;occupied=$true}}}
$bulk=Api 'POST' '/ops/flats/bulk' @{flats=$flats;requestKey=(Key)} $t;Check ($bulk.Status -eq 200 -and $bulk.Body.added -eq 100) 'Flat generator saves 10 floors x 10 flats in one call'
Check ((Done 'flats' $t).Body.current -eq 'payment') 'Flats step done'
Check ((Done 'payment' $t).Status -eq 400) 'Payment step refuses empty payment details'
Check ((Api 'POST' '/ops/settings/payment' @{payee='TEST Assoc';upi='test@upi';bank='';account='';ifsc='';requestKey=(Key)} $t).Status -eq 200) 'Save payment details'
Check ((Done 'payment' $t).Body.current -eq 'maintenance') 'Payment step done'
$month=(Get-Date).ToUniversalTime().AddHours(5.5).AddMonths(1).ToString('yyyy-MM')
Check ((Api 'POST' '/billing/rules' @{amount=2500;effective=$month;billingDay=1;dueDay=10} $t).Status -eq 200) 'Save maintenance amount'
Check ((Done 'maintenance' $t).Body.current -eq 'late_fee') 'Maintenance step done'
Check ((Done 'late_fee' $t).Body.current -eq 'opening_balance') 'Late fee is optional'
$first=(Get-Date).ToUniversalTime().AddHours(5.5).ToString('yyyy-MM-01')
Check ((Api 'POST' '/ops/opening' @{date=$first;amount=0} $t).Status -eq 200) 'Existing balance: starting from zero'
Check ((Done 'opening_balance' $t).Body.current -eq 'old_dues') 'Existing balance step done'
Check ((Done 'old_dues' $t).Body.current -eq 'invite') 'Old dues: none'
$inv=Api 'POST' '/invites' @{requestKey=(Key)} $t;Check ($inv.Status -eq 200) 'Create invite code'
$s=Done 'invite' $t;Check ($s.Body.requiredComplete -eq $true -and $s.Body.setupComplete -eq $false) 'Required steps complete; readiness checklist still shown'
Check ((Api 'GET' '/members' $null $t).Status -eq 200) 'Features unlock after setup'
Check ((Api 'GET' '/ops/subscription' $null $t).Body.price -eq 349) '100 flats are priced at Rs 349'
$j=Api 'POST' '/auth/join' @{invite=$inv.Body.code;flatLabel='T1-101';name='TEST Setup Resident';mobile=$mobileR;pin=$pin;residentType='OWNER'};Check ($j.Status -eq 200) 'Resident joins a generated flat'
$m=(Api 'GET' '/members' $null $t).Body|Where-Object {$_.mobile -eq $mobileR}
Check ((Api 'POST' "/members/$($m.id)/approve" @{verified=$true} $t).Status -eq 200) 'Admin approves resident'
$r=(Api 'POST' '/auth/login' @{mobile=$mobileR;pin=$pin;role='RESIDENT'}).Body.token
$rs=Api 'GET' '/setup' $null $r;Check ($rs.Body.current -eq 'terms' -and $rs.Body.total -eq 5) 'Resident wizard starts at Terms (5 steps)'
Check ((Api 'POST' '/payments' @{} $r).Status -eq 428) 'Resident features locked until setup is done'
foreach($k in 'terms','profile','vehicles','how_to_pay','stay_informed'){$rs=Done $k $r}
Check ($rs.Body.setupComplete -eq $true) 'Resident setup complete'
$row=(Api 'GET' '/setup/members' $null $t).Body|Where-Object {$_.label -eq 'T1-101'};Check ($row.state -eq 'COMPLETE') 'Admin sees the flat as setup complete'
Check ((Api 'POST' "/members/$($m.id)/treasurer" @{} $t).Status -eq 200) 'Assign treasurer'
$tr=(Api 'POST' '/auth/login' @{mobile=$mobileR;pin=$pin;role='TREASURER'}).Body.token
$ts=Api 'GET' '/setup' $null $tr;Check ($ts.Body.current -eq 'treasurer_rules') 'Treasurer wizard skips the Terms step already accepted'
Write-Host "All $script:checks guided setup checks passed."
