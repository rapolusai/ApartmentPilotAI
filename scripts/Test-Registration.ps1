[CmdletBinding()]
param([switch]$AllowSyntheticRecords)
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'Explicitly allow retained labelled localhost fixtures.'}
if(!$env:PGPASSWORD){throw 'Set PGPASSWORD locally; never put it in this script.'}
$base='http://127.0.0.1:8081/api/v1'
$psql='C:\Program Files\PostgreSQL\18\bin\psql.exe'
$script:checks=0
function Api($method,$path,$body=$null,$token='') {
 $args=@{Method=$method;Uri="$base$path";ContentType='application/json';SkipHttpErrorCheck=$true;TimeoutSec=30;Headers=@{}}
 if($body){$args.Body=$body|ConvertTo-Json -Depth 8 -Compress};if($token){$args.Headers.Authorization="Bearer $token"}
 $r=Invoke-WebRequest @args
 return @{Status=[int]$r.StatusCode;Body=($r.Content|ConvertFrom-Json)}
}
function Check($condition,$label) {if(!$condition){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function Sql($sql) {
 $r=& $psql -X -w -h 127.0.0.1 -U postgres -d partmentpilotai_dev -t -A -v ON_ERROR_STOP=1 -c $sql
 if($LASTEXITCODE){throw 'Fixture database check failed'}
 return ($r -join "`n").Trim()
}
function Mobile {return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString()}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$mobile=Mobile
$body=@{name='REG QA Admin';mobile=$mobile;pin=$pin;apartmentName="REG QA $stamp";city='Hyderabad';flats=5;address='Synthetic Street 1';pinCode='500001'}
$a=Api POST '/auth/register' $body
Check ($a.Status -eq 200) 'Admin registration succeeds on localhost'
$tenant=[guid]$a.Body.account.tenantId;$admin=[guid]$a.Body.account.id;$token=$a.Body.token
Check ($a.Body.account.role -eq 'ADMIN') 'Admin identity is derived by server'
Check ((Sql "select address||'|'||pincode from apartmentpilotai.ap_tenant where id='$tenant'") -eq 'Synthetic Street 1|500001') 'Address and PIN code persisted'
Check ((Sql "select count(*) from apartmentpilotai.ap_flat where tenant_id='$tenant'") -eq '5') 'Exactly five initial flat records persisted'
$before=Sql "select count(*) from apartmentpilotai.ap_tenant"
$duplicate=Api POST '/auth/register' $body
Check ($duplicate.Status -eq 409) 'Duplicate admin mobile returns conflict'
Check ((Sql 'select count(*) from apartmentpilotai.ap_tenant') -eq $before) 'Duplicate registration creates no orphan tenant'
$invalid=$body.Clone();$invalid.pinCode='12345'
Check ((Api POST '/auth/register' $invalid).Status -eq 400) 'Malformed PIN code rejected by server'
$invalid=$body.Clone();$invalid.flats=51
Check ((Api POST '/auth/register' $invalid).Status -eq 400) 'Out-of-range flats rejected by server'
$b=Api POST '/auth/register' @{name='REG QA other Admin';mobile=(Mobile);pin=$pin;apartmentName="REG QA other $stamp";city='Pune';flats=5}
Check ($b.Status -eq 200) 'Legacy local registration remains compatible'
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $token
Check ($invite.Status -eq 200) 'Admin creates a real invite'
$preview=Api GET "/auth/invites/$($invite.Body.code)"
Check ($preview.Status -eq 200 -and $preview.Body.city -eq 'Hyderabad' -and $preview.Body.flats.Count -eq 5) 'Invite preview returns server apartment, city and available flats'
Check ((Api GET '/auth/invites/INVALID-CODE').Status -eq 404) 'Invalid invite fails closed'
$join=@{invite=$invite.Body.code;flatLabel='A-101';name='REG QA Resident';mobile=(Mobile);pin=$pin;residentType='OWNER'}
$r=Api POST '/auth/join' $join
Check ($r.Status -eq 200 -and $r.Body.status -eq 'PENDING') 'Join starts pending and does not authenticate'
Check (!$r.Body.token -and $r.Body.receipt.Length -eq 43) 'Join receives only an opaque status receipt'
$hash=Sql "select token_hash from apartmentpilotai.ap_join_receipt where user_id=(select id from apartmentpilotai.ap_user where tenant_id='$tenant' and mobile='$($join.mobile)') order by created_at desc limit 1"
Check ($hash.Length -eq 64 -and $hash -ne $r.Body.receipt) 'Only receipt hash is stored in PostgreSQL'
$status=Api POST '/auth/join-status' @{receipt=$r.Body.receipt}
Check ($status.Body.status -eq 'PENDING' -and $status.Body.flatLabel -eq 'A-101' -and $status.Body.apartmentName -eq $body.apartmentName) 'Receipt retrieves only the bound join request'
Check ((Api GET '/account' $null $r.Body.receipt).Status -eq 401) 'Status receipt cannot authenticate application access'
Check ((Api POST '/auth/join-status' @{receipt=[guid]::NewGuid().ToString()}).Status -eq 400) 'Guessed record UUID is not accepted as a receipt'
Check ((Api POST '/auth/join-status' @{receipt=('x'*43)}).Status -eq 404) 'Unknown opaque receipt returns no private record'
Check ((Api POST '/auth/login' @{mobile=$join.mobile;pin=$pin;role='RESIDENT'}).Status -eq 403) 'Pending resident cannot log in'
$replay=Api POST '/auth/join' $join
Check ($replay.Status -eq 200 -and $replay.Body.status -eq 'PENDING') 'Authenticated identical pending retry is safe'
Check ((Sql "select count(*) from apartmentpilotai.ap_user where mobile='$($join.mobile)'") -eq '1') 'Retry does not duplicate resident record'
$occupied=$join.Clone();$occupied.mobile=Mobile
Check ((Api POST '/auth/join' $occupied).Status -eq 409) 'Second pending claimant cannot take the same flat'
$wrong=$join.Clone();$wrong.pin='0000'
Check ((Api POST '/auth/join' $wrong).Status -eq 409) 'Wrong PIN cannot obtain another receipt by replay'
$member=(Api GET '/members' $null $token).Body|Where-Object {$_.mobile -eq $join.mobile}
$id=[guid]$member.id
Check ((Api POST "/members/$id/approve" @{verified=$true} $b.Body.token).Status -eq 409) 'Other tenant cannot approve this request'
Check ((Api POST "/members/$id/reject" @{reason='Synthetic wrong tenant'} $b.Body.token).Status -eq 404) 'Other tenant cannot reject this request'
Check ((Api POST "/members/$id/reject" @{reason='Synthetic missing ownership evidence'} $token).Status -eq 200) 'Own Admin can reject pending claim'
$rejected=Api POST '/auth/join-status' @{receipt=$r.Body.receipt}
Check ($rejected.Body.status -eq 'REJECTED' -and $rejected.Body.reason -eq 'Synthetic missing ownership evidence') 'Resident receipt shows persisted rejection reason'
$resubmit=Api POST '/auth/join' $join
Check ($resubmit.Status -eq 200 -and $resubmit.Body.status -eq 'PENDING') 'Rejected resident can resubmit with own PIN'
Check ((Api POST '/auth/join-status' @{receipt=$r.Body.receipt}).Status -eq 404) 'Resubmission revokes previous status receipts'
Check ((Api POST "/members/$id/approve" @{verified=$false} $token).Status -eq 400) 'Approval requires explicit resident/flat verification'
Check ((Api POST "/members/$id/approve" @{verified=$true} $token).Status -eq 200) 'Own Admin can approve resubmitted claim'
Check ((Api POST '/auth/join-status' @{receipt=$resubmit.Body.receipt}).Body.status -eq 'ACTIVE') 'Check status reflects real approval without issuing a login token'
$login=Api POST '/auth/login' @{mobile=$join.mobile;pin=$pin;role='RESIDENT'}
Check ($login.Status -eq 200 -and $login.Body.account.tenantId -eq $tenant.ToString()) 'Approved resident logs into server-derived apartment'
Check ((Api POST '/auth/join' $join).Status -eq 409) 'Active account cannot be overwritten through join replay'
Check ((Api POST "/members/$id/approve" @{verified=$true} $login.Body.token).Status -eq 403) 'Resident cannot grant approval'
Sql "update apartmentpilotai.ap_join_receipt set expires_at=now()-interval '1 second' where token_hash='$hash'" | Out-Null
# That earlier receipt was revoked on resubmission; expire all retained receipts for this test user.
Sql "update apartmentpilotai.ap_join_receipt set expires_at=now()-interval '1 second' where user_id='$id' and user_id in(select id from apartmentpilotai.ap_user where tenant_id='$tenant' and name='REG QA Resident')" | Out-Null
Check ((Api POST '/auth/join-status' @{receipt=$resubmit.Body.receipt}).Status -eq 404) 'Expired receipt cannot retrieve private status'
$raceJobs=@()
try {
 foreach($i in 1,2) {
  $claim=$join.Clone();$claim.mobile=Mobile;$claim.flatLabel='A-102';$claim.name="REG QA race $i"
  $raceJobs+=Start-Job -ArgumentList $base,($claim|ConvertTo-Json -Compress) -ScriptBlock {
   param($url,$json)
   $response=Invoke-WebRequest -Method POST -Uri "$url/auth/join" -ContentType 'application/json' -Body $json -SkipHttpErrorCheck -TimeoutSec 30
   [int]$response.StatusCode
  }
 }
 $raceJobs|Wait-Job -Timeout 45|Out-Null
 if(@($raceJobs|Where-Object {$_.State -ne 'Completed'}).Count){throw 'Race workers did not complete'}
 $statuses=@($raceJobs|Receive-Job)
 Check (@($statuses|Where-Object {$_ -eq 200}).Count -eq 1 -and @($statuses|Where-Object {$_ -eq 409}).Count -eq 1) 'Concurrent same-flat claims produce one winner and one conflict'
 Check ((Sql "select count(*) from apartmentpilotai.ap_user u join apartmentpilotai.ap_flat f on f.id=u.flat_id and f.tenant_id=u.tenant_id where u.tenant_id='$tenant' and f.label='A-102' and u.status='PENDING'") -eq '1') 'Concurrent requests persist exactly one pending claimant'
 Check ((Sql "select count(*) from apartmentpilotai.ap_join_receipt r join apartmentpilotai.ap_user u on u.id=r.user_id join apartmentpilotai.ap_flat f on f.id=u.flat_id where u.tenant_id='$tenant' and f.label='A-102'") -eq '1') 'Losing concurrent transaction leaves no status receipt'
} finally {if($raceJobs.Count){$raceJobs|Stop-Job;$raceJobs|Remove-Job}}
Write-Host "Completed $script:checks real HTTP/PostgreSQL registration checks. REG QA $stamp retained. Credentials and receipts not printed."
