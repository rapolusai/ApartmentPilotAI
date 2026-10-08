[CmdletBinding()]
param([switch]$AllowSyntheticRecords)
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'Explicitly allow retained labelled localhost fixtures.'}
$base='http://127.0.0.1:8081/api/v1';$script:checks=0
function Api($method,$path,$body=$null,$token='') {
 $args=@{Method=$method;Uri="$base$path";ContentType='application/json';SkipHttpErrorCheck=$true;TimeoutSec=30;Headers=@{}}
 if($body){$args.Body=$body|ConvertTo-Json -Depth 8 -Compress};if($token){$args.Headers.Authorization="Bearer $token"}
 $r=Invoke-WebRequest @args;return @{Status=[int]$r.StatusCode;Body=($r.Content|ConvertFrom-Json)}
}
function Check($condition,$label){if(!$condition){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function Mobile {return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString()}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$a=Api POST '/auth/register' @{name='FLAT QA Admin';mobile=(Mobile);pin=$pin;apartmentName="FLAT QA $stamp";city='Pune';flats=5}
Check ($a.Status -eq 200) 'Admin fixture registered locally'
$b=Api POST '/auth/register' @{name='FLAT QA Other';mobile=(Mobile);pin=$pin;apartmentName="FLAT QA other $stamp";city='Pune';flats=5}
Check ($b.Status -eq 200) 'Other tenant fixture registered locally'
$flats=Api GET '/ops/flats' $null $a.Body.token;$fa=$flats.Body[0];$fb=$flats.Body[1]
$other=(Api GET '/ops/flats' $null $b.Body.token).Body[0]
$detail=Api GET "/ops/flats/$($fa.id)" $null $a.Body.token
Check ($detail.Status -eq 200 -and $detail.Body.label -eq $fa.label -and $detail.Body.block -eq 'A' -and $null -eq $detail.Body.memberId) 'Own unassigned flat returns persisted details without invented resident'
Check ((Api GET "/ops/flats/$($fa.id)").Status -eq 401) 'Anonymous detail is refused'
Check ((Api GET "/ops/flats/$($other.id)" $null $a.Body.token).Status -eq 404) 'Foreign flat detail is parent-checked'
Check ((Api GET "/ops/flats/$([guid]::NewGuid())" $null $a.Body.token).Status -eq 404) 'Missing flat returns not found rather than fallback'
Check ((Api GET '/ops/flats/not-a-uuid' $null $a.Body.token).Status -eq 400) 'Malformed flat ID is refused'
$month=Get-Date -Format 'yyyy-MM';$day=Get-Date -Format 'yyyy-MM-dd'
Check ((Api POST '/billing/rules' @{amount=1000;effective=$month;billingDay=1;dueDay=10} $a.Body.token).Status -eq 200) 'Existing billing rule accepts fixture'
Check ((Api POST "/billing/generate?month=$month" @{} $a.Body.token).Status -eq 200) 'Existing billing generation runs'
$bills=(Api GET "/bills?month=$month" $null $a.Body.token).Body
Check ($bills.Count -eq 5) 'Five current flat bills persisted'
foreach($flat in @($fa,$fb)) {
 $bill=@($bills|Where-Object {$_.flatId -eq $flat.id})[0]
 $p=Api POST '/payments' @{billId=$bill.id;amount=100;mode='CASH';paidOn=$day;note='Synthetic scoped history';requestKey=[guid]::NewGuid().ToString()} $a.Body.token
 Check ($p.Status -eq 200 -and $p.Body.status -eq 'PENDING') 'Payment declaration remains pending, not a fake approval'
}
Check ((Api GET '/payments' $null $a.Body.token).Body.Count -eq 2) 'Unfiltered staff history retains both flats'
$scoped=Api GET "/payments?flatId=$($fa.id)" $null $a.Body.token
Check ($scoped.Status -eq 200 -and $scoped.Body.Count -eq 1 -and $scoped.Body[0].flatLabel -eq $fa.label) 'Selected flat history excludes other flat payments'
Check ((Api GET "/payments?flatId=$($other.id)" $null $a.Body.token).Status -eq 404) 'Foreign flat history is refused'
Check ((Api GET "/payments?flatId=$([guid]::NewGuid())" $null $a.Body.token).Status -eq 404) 'Missing parent cannot enumerate history'
Check ((Api GET '/payments?flatId=bad' $null $a.Body.token).Status -eq 400) 'Malformed history filter refused'
$invite=(Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $a.Body.token).Body
$mobile=Mobile
Check ((Api POST '/auth/join' @{invite=$invite.code;flatLabel=$fa.label;name='FLAT QA Resident';mobile=$mobile;pin=$pin;residentType='OWNER'}).Status -eq 200) 'Resident claim created on selected flat'
$member=@((Api GET '/members' $null $a.Body.token).Body|Where-Object {$_.mobile -eq $mobile})[0]
$pending=Api GET "/ops/flats/$($fa.id)" $null $a.Body.token
Check ($pending.Body.status -eq 'PENDING' -and $pending.Body.name -eq 'FLAT QA Resident') 'Flat detail reflects pending identity from server'
Check ((Api POST "/members/$($member.id)/approve" @{verified=$true} $a.Body.token).Status -eq 200) 'Admin explicitly verifies fixture resident'
$active=Api GET "/ops/flats/$($fa.id)" $null $a.Body.token
Check ($active.Body.status -eq 'ACTIVE') 'Flat detail reflects active identity after approval'
$resident=Api POST '/auth/login' @{mobile=$mobile;pin=$pin;role='RESIDENT'}
Check ($resident.Status -eq 200) 'Approved Resident authenticates'
Check ((Api GET "/ops/flats/$($fa.id)" $null $resident.Body.token).Status -eq 403) 'Resident cannot open private Admin flat details'
Check ((Api GET "/payments?flatId=$($fb.id)" $null $resident.Body.token).Status -eq 403) 'Resident cannot select another same-tenant flat history'
Check ((Api GET "/payments?flatId=$($fa.id)" $null $resident.Body.token).Body.Count -eq 1) 'Resident own-flat filter works'
Check ((Api GET '/payments' $null $resident.Body.token).Body.Count -eq 1) 'Legacy unfiltered Resident history remains own-flat only'
Check ((Api POST "/members/$($member.id)/treasurer" @{} $a.Body.token).Status -eq 200) 'Treasurer fixture granted by Admin'
$treasurer=Api POST '/auth/login' @{mobile=$mobile;pin=$pin;role='TREASURER'}
Check ((Api GET "/ops/flats/$($fa.id)" $null $treasurer.Body.token).Status -eq 403) 'Treasurer cannot open Admin-only flat details'
Check ((Api GET "/payments?flatId=$($fb.id)" $null $treasurer.Body.token).Body.Count -eq 1) 'Treasurer retains authorized financial history filtering'
Write-Host "Completed $script:checks bounded flat detail/history checks. FLAT QA $stamp retained. Credentials not printed."
