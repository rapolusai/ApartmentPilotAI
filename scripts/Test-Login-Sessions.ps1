# Creates and retains labelled synthetic localhost fixtures. No schema/data reset.
[CmdletBinding()]
param([string]$BaseUrl='http://127.0.0.1:8081/api/v1',[switch]$AllowSyntheticRecords,
 [string]$Psql='C:\Program Files\PostgreSQL\18\bin\psql.exe',
 [string]$Database='partmentpilotai_dev',[string]$DbUser='postgres')
$ErrorActionPreference='Stop'
$uri=[uri]$BaseUrl
if(!$AllowSyntheticRecords -or $uri.Scheme -ne 'http' -or $uri.Host -notin @('localhost','127.0.0.1') -or $uri.UserInfo) {throw 'Use -AllowSyntheticRecords against the localhost development backend only.'}
if(!(Test-Path -LiteralPath $Psql) -or [string]::IsNullOrEmpty($env:PGPASSWORD)) {throw 'Configure PGPASSWORD locally for the PostgreSQL session checks; do not put it in source or chat.'}
$script:checks=0
function Api([string]$method,[string]$path,[object]$body=$null,[string]$token='') {
 $headers=@{};if($token){$headers.Authorization="Bearer $token"}
 $args=@{Method=$method;Uri="$BaseUrl$path";Headers=$headers;ContentType='application/json';TimeoutSec=30}
 if($null -ne $body){$args.Body=$body|ConvertTo-Json -Depth 8 -Compress}
 try {return [pscustomobject]@{Status=200;Body=(Invoke-RestMethod @args)}}
 catch {if(!$_.Exception.Response){throw};return [pscustomobject]@{Status=[int]$_.Exception.Response.StatusCode;Body=$null}}
}
function Check([bool]$pass,[string]$label) {if(!$pass){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function Sql([string]$query) {
 $query=$query -replace '\b(ap_session|ap_user)\b','apartmentpilotai.$1'
 $answer=& $Psql -X -w -h 127.0.0.1 -p 5432 -U $DbUser -d $Database -A -t -v ON_ERROR_STOP=1 -c $query
 if($LASTEXITCODE -ne 0){throw 'PostgreSQL verification failed.'}
 return ($answer -join "`n").Trim()
}
function Digest([string]$token) {return [Convert]::ToHexString([Security.Cryptography.SHA256]::HashData([Text.Encoding]::UTF8.GetBytes($token))).ToLowerInvariant()}
function Mobile {return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString()}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$mobileA=Mobile;$mobileB=Mobile;$mobileR=Mobile
$a=Api POST '/auth/register' @{name='LOGIN QA Admin A';mobile=$mobileA;pin=$pin;apartmentName="LOGIN QA A $stamp";city='Hyderabad';flats=5}
Check ($a.Status -eq 200) 'Create labelled login fixture A';$tokenA=$a.Body.token
$b=Api POST '/auth/register' @{name='LOGIN QA Admin B';mobile=$mobileB;pin=$pin;apartmentName="LOGIN QA B $stamp";city='Pune';flats=5}
Check ($b.Status -eq 200) 'Create labelled isolated login fixture B'
$tenant=[guid]$a.Body.account.tenantId;$user=[guid]$a.Body.account.id
Check ((Api GET '/me').Status -eq 401) 'Anonymous account access is denied'
Check ((Api GET '/me' $null 'not-a-valid-token').Status -eq 401) 'Invalid bearer token is denied'
Check ((Api POST '/auth/login' @{mobile='123';pin=$pin;role='ADMIN'}).Status -eq 400) 'Malformed mobile is rejected server-side'
Check ((Api POST '/auth/login' @{mobile=$mobileA;pin='12';role='ADMIN'}).Status -eq 400) 'Malformed PIN is rejected server-side'
Check ((Api POST '/auth/login' @{mobile=$mobileA;pin=$pin;role='ROOT'}).Status -eq 400) 'Unknown client login type is rejected'
Check ((Api POST '/auth/login' @{mobile=$mobileA;pin='000000';role='ADMIN'}).Status -eq 401) 'Incorrect PIN is denied'
Check ((Api POST '/auth/login' @{mobile=$mobileA;pin=$pin;role='TREASURER'}).Status -eq 401) 'Role selector cannot grant Treasurer access'
$login=Api POST '/auth/login' @{mobile=$mobileA;pin=$pin;role='ADMIN';tenantId=$b.Body.account.tenantId}
Check ($login.Status -eq 200 -and $login.Body.account.role -eq 'ADMIN' -and $login.Body.account.tenantId -eq $tenant) 'Authenticated identity and tenant come from server, not supplied tenant'
$hash=Digest $login.Body.token
Check ((Sql "select count(*) from ap_session where token_hash='$hash' and user_id='$user' and expires_at>now()") -eq '1') 'Issued session persists as token hash with future expiry'
Check ((Sql "select pin_hash like '`$2%' and length(pin_hash)=60 from ap_user where id='$user' and tenant_id='$tenant'") -eq 't') 'PIN persists as BCrypt hash, not plaintext'
$logout=Api POST '/logout' @{} $login.Body.token
Check ($logout.Status -eq 200 -and (Api GET '/me' $null $login.Body.token).Status -eq 401) 'Logout revokes presented session immediately'
Check ((Api GET '/me' $null $tokenA).Status -eq 200) 'Logout does not revoke separate session for same account'
$expiring=Api POST '/auth/login' @{mobile=$mobileA;pin=$pin;role='ADMIN'}
Check ($expiring.Status -eq 200) 'Create session for true-expiry test'
$expiryHash=Digest $expiring.Body.token
Sql "update ap_session set expires_at=now()-interval '1 second' where token_hash='$expiryHash' and user_id='$user'" | Out-Null
Check ((Api GET '/me' $null $expiring.Body.token).Status -eq 401) 'Real database-expired session cannot access account'
Check ((Api GET '/me' $null $tokenA).Status -eq 200) 'Expiring one fixture session preserves another session'
Sql "update ap_user set status='DISABLED' where id='$user' and tenant_id='$tenant' and name='LOGIN QA Admin A'" | Out-Null
try {
 Check ((Api GET '/me' $null $tokenA).Status -eq 401) 'Disabled account loses access even with previously valid session'
 Check ((Api POST '/auth/login' @{mobile=$mobileA;pin=$pin;role='ADMIN'}).Status -eq 403) 'Disabled account cannot sign in again'
} finally {Sql "update ap_user set status='ACTIVE' where id='$user' and tenant_id='$tenant' and name='LOGIN QA Admin A'" | Out-Null}
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $tokenA
Check ($invite.Status -eq 200) 'Prepare invitation for login role fixture'
$join=Api POST '/auth/join' @{invite=$invite.Body.code;flatLabel='A-101';name='LOGIN QA Resident';mobile=$mobileR;pin=$pin;residentType='OWNER'}
Check ($join.Status -eq 200) 'Create pending resident fixture'
Check ((Api POST '/auth/login' @{mobile=$mobileR;pin=$pin;role='RESIDENT'}).Status -eq 403) 'Pending resident cannot sign in'
$members=Api GET '/members' $null $tokenA
$resident=@($members.Body|Where-Object {$_.mobile -eq $mobileR})[0]
Check ((Api POST "/members/$($resident.id)/approve" @{verified=$true} $tokenA).Status -eq 200) 'Approve only synthetic resident'
$r=Api POST '/auth/login' @{mobile=$mobileR;pin=$pin;role='RESIDENT'}
Check ($r.Status -eq 200 -and $r.Body.account.role -eq 'RESIDENT') 'Approved Resident login returns server role'
Check ((Api GET '/members' $null $r.Body.token).Status -eq 403) 'Resident cannot access Admin members endpoint'
Check ((Api POST "/members/$($resident.id)/treasurer" @{} $tokenA).Status -eq 200) 'Grant Treasurer on labelled resident fixture'
Check ((Api GET '/me' $null $r.Body.token).Status -eq 401) 'Role change revokes old resident session'
Check ((Api POST '/auth/login' @{mobile=$mobileR;pin=$pin;role='RESIDENT'}).Status -eq 401) 'Old login type cannot restore revoked role'
$t=Api POST '/auth/login' @{mobile=$mobileR;pin=$pin;role='TREASURER'}
Check ($t.Status -eq 200 -and $t.Body.account.role -eq 'TREASURER') 'Treasurer login reflects persisted server role'
Check ((Api GET '/members' $null $t.Body.token).Status -eq 403) 'Treasurer cannot access Admin members endpoint'
Check ((Api GET '/me' $null $b.Body.token).Body.tenantId -ne $tenant) 'Other tenant session remains isolated'
$limitedMobile=Mobile
for($i=1;$i -le 10;$i++) {Check ((Api POST '/auth/login' @{mobile=$limitedMobile;pin=$pin;role='RESIDENT'}).Status -eq 401) "Failed login attempt $i is counted"}
Check ((Api POST '/auth/login' @{mobile=$limitedMobile;pin=$pin;role='RESIDENT'}).Status -eq 429) 'Eleventh mobile attempt is rate limited without bypass/reset'
Write-Host "Completed $script:checks Login/session HTTP+PostgreSQL checks. LOGIN QA A/B $stamp retained. Credentials were not printed."
