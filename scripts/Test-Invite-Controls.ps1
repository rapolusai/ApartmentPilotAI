# Retains labelled synthetic records. Never resets or deletes the development database.
[CmdletBinding()]
param([string]$BaseUrl='http://127.0.0.1:8081/api/v1',[switch]$AllowSyntheticRecords)
$ErrorActionPreference='Stop'
if (!$AllowSyntheticRecords) { throw 'This test creates synthetic apartments. Use -AllowSyntheticRecords against localhost only.' }
$uri=[uri]$BaseUrl
if ($uri.Scheme -ne 'http' -or $uri.Host -notin @('localhost','127.0.0.1') -or $uri.UserInfo) { throw 'Only the localhost development backend is allowed.' }
$script:Count=0
function Api([string]$method,[string]$path,[object]$body=$null,[string]$token='') {
 $headers=@{};if($token){$headers.Authorization="Bearer $token"}
 $args=@{Method=$method;Uri="$BaseUrl$path";Headers=$headers;ContentType='application/json';TimeoutSec=30}
 if($null -ne $body){$args.Body=$body|ConvertTo-Json -Depth 8 -Compress}
 try { $value=Invoke-RestMethod @args;return [pscustomobject]@{Status=200;Body=$value} }
 catch { if(-not $_.Exception.Response){throw};return [pscustomobject]@{Status=[int]$_.Exception.Response.StatusCode;Body=$null} }
}
function Check([bool]$pass,[string]$label) {
 if(-not $pass){throw "FAIL: $label"}
 $script:Count++;Write-Host "PASS: $label"
}
function Mobile { return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString() }
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$a=Api 'POST' '/auth/register' @{name='INVITE QA Admin A';mobile=(Mobile);pin=$pin;apartmentName="INVITE QA A $stamp";city='Hyderabad';flats=5}
Check ($a.Status -eq 200) 'Create isolated invite QA apartment A';$adminA=$a.Body.token
$b=Api 'POST' '/auth/register' @{name='INVITE QA Admin B';mobile=(Mobile);pin=$pin;apartmentName="INVITE QA B $stamp";city='Pune';flats=5}
Check ($b.Status -eq 200) 'Create isolated invite QA apartment B';$adminB=$b.Body.token
$empty=Api 'GET' '/invites/current' $null $adminA
Check ($empty.Status -eq 200 -and $empty.Body.exists -eq $false -and $empty.Body.displayConfigured -eq $true) 'Admin sees truthful no-invite state'
$noLeak=Api 'GET' '/invites/current' $null $adminB
Check ($noLeak.Status -eq 200 -and $noLeak.Body.exists -eq $false) 'Another tenant cannot see apartment A invite'
$key=[guid]::NewGuid().ToString()
$first=Api 'POST' '/invites' @{requestKey=$key} $adminA
Check ($first.Status -eq 200 -and $first.Body.code -and $first.Body.codeAvailable -and $first.Body.valid) 'Admin issues an encrypted displayable code'
$code=$first.Body.code
$lookup=Api 'GET' "/auth/invites/$code"
Check ($lookup.Status -eq 200) 'Issued code resolves available flats'
$retry=Api 'POST' '/invites' @{requestKey=$key} $adminA
Check ($retry.Status -eq 200 -and $retry.Body.code -eq $code) 'Same rotation key returns the same code without rotating'
$current=Api 'GET' '/invites/current' $null $adminA
Check ($current.Status -eq 200 -and $current.Body.code -eq $code) 'Admin revisit redisplays the same current code'
$bad=Api 'PUT' '/invites/current/expiry' @{expiry=(Get-Date).AddDays(-1).ToString('yyyy-MM-dd')} $adminA
Check ($bad.Status -eq 400) 'Past expiry is rejected'
$far=Api 'PUT' '/invites/current/expiry' @{expiry=(Get-Date).AddYears(2).ToString('yyyy-MM-dd')} $adminA
Check ($far.Status -eq 400) 'Unbounded expiry is rejected'
$until=(Get-Date).AddDays(14).ToString('yyyy-MM-dd')
$saved=Api 'PUT' '/invites/current/expiry' @{expiry=$until} $adminA
Check ($saved.Status -eq 200 -and $saved.Body.code -eq $code -and $saved.Body.expiresOn -eq $until) 'Future expiry persists as the inclusive local date without changing the code'
$savedRetry=Api 'PUT' '/invites/current/expiry' @{expiry=$until} $adminA
Check ($savedRetry.Status -eq 200 -and $savedRetry.Body.expiresAt -eq $saved.Body.expiresAt) 'Expiry retry leaves the same deadline'
$revoked=Api 'POST' '/invites/current/revoke' @{} $adminA
Check ($revoked.Status -eq 200 -and $revoked.Body.active -eq $false) 'Admin revokes the code'
$closed=Api 'GET' "/auth/invites/$code"
Check ($closed.Status -eq 404) 'Revoked code cannot be used to join'
$revokeRetry=Api 'POST' '/invites/current/revoke' @{} $adminA
Check ($revokeRetry.Status -eq 200 -and $revokeRetry.Body.active -eq $false) 'Revoke retry is idempotent'
$activated=Api 'POST' '/invites/current/activate' @{} $adminA
Check ($activated.Status -eq 200 -and $activated.Body.active -and $activated.Body.code -eq $code) 'Admin reactivates the same unexpired code'
$open=Api 'GET' "/auth/invites/$code"
Check ($open.Status -eq 200) 'Reactivated code works until its saved expiry'
$residentMobile=Mobile
$joined=Api 'POST' '/auth/join' @{invite=$code;flatLabel='A-101';name='INVITE QA Resident';mobile=$residentMobile;pin=$pin;residentType='OWNER'}
Check ($joined.Status -eq 200 -and $joined.Body.status -eq 'PENDING') 'Resident signup remains pending'
$members=Api 'GET' '/members' $null $adminA
$resident=@($members.Body|Where-Object {$_.mobile -eq $residentMobile})[0]
$approved=Api 'POST' "/members/$($resident.id)/approve" @{verified=$true} $adminA
Check ($approved.Status -eq 200) 'Admin independently approves the resident'
$residentLogin=Api 'POST' '/auth/login' @{mobile=$residentMobile;pin=$pin;role='RESIDENT'}
Check ($residentLogin.Status -eq 200) 'Approved resident signs in';$residentToken=$residentLogin.Body.token
$residentRead=Api 'GET' '/invites/current' $null $residentToken
Check ($residentRead.Status -eq 403) 'Resident cannot read the reusable invite code'
$residentRevoke=Api 'POST' '/invites/current/revoke' @{} $residentToken
Check ($residentRevoke.Status -eq 403) 'Resident cannot revoke the apartment invite'
$otherRevoke=Api 'POST' '/invites/current/revoke' @{} $adminB
Check ($otherRevoke.Status -eq 404) 'Foreign Admin cannot alter an apartment without an invite'
$nextKey=[guid]::NewGuid().ToString()
$rotated=Api 'POST' '/invites' @{requestKey=$nextKey} $adminA
Check ($rotated.Status -eq 200 -and $rotated.Body.code -ne $code) 'Admin regenerates a different code'
$oldLookup=Api 'GET' "/auth/invites/$code"
Check ($oldLookup.Status -eq 404) 'Regeneration invalidates the previous code immediately'
$newLookup=Api 'GET' "/auth/invites/$($rotated.Body.code)"
Check ($newLookup.Status -eq 200) 'The regenerated code is usable'
$rotateRetry=Api 'POST' '/invites' @{requestKey=$nextKey} $adminA
Check ($rotateRetry.Status -eq 200 -and $rotateRetry.Body.code -eq $rotated.Body.code) 'Regeneration retry does not create another code'
$residentStill=Api 'GET' '/me' $null $residentToken
Check ($residentStill.Status -eq 200 -and $residentStill.Body.role -eq 'RESIDENT') 'Approved account retains access after regeneration'
$foreignCurrent=Api 'GET' '/invites/current' $null $adminB
Check ($foreignCurrent.Status -eq 200 -and $foreignCurrent.Body.exists -eq $false) 'Another tenant still cannot view the code'
$raceKeys=@([guid]::NewGuid().ToString(),[guid]::NewGuid().ToString())
$raceJobs=@(foreach($raceKey in $raceKeys) {
 Start-Job -ArgumentList $BaseUrl,$adminA,$raceKey -ScriptBlock {
  param($localBase,$localToken,$localKey)
  Start-Sleep -Seconds 2
  try {
   $reply=Invoke-RestMethod -Method POST -Uri "$localBase/invites" -Headers @{Authorization="Bearer $localToken"} -ContentType 'application/json' -Body (@{requestKey=$localKey}|ConvertTo-Json -Compress) -TimeoutSec 30
   [pscustomobject]@{Status=200;Code=$reply.code}
  } catch {
   [pscustomobject]@{Status=if($_.Exception.Response){[int]$_.Exception.Response.StatusCode}else{0};Code=$null}
  }
 }
})
$raceJobs | Wait-Job -Timeout 40 | Out-Null
$raceResults=@($raceJobs | Receive-Job)
Check ($raceResults.Count -eq 2 -and @($raceResults|Where-Object {$_.Status -eq 200 -and $_.Code}).Count -eq 2 -and $raceResults[0].Code -ne $raceResults[1].Code) 'Concurrent rotation requests each complete with distinct codes'
$raceCurrent=Api 'GET' '/invites/current' $null $adminA
Check ($raceCurrent.Status -eq 200 -and $raceCurrent.Body.code -in @($raceResults|ForEach-Object {$_.Code})) 'Current invite resolves to one serialized rotation winner'
$raceLookups=@(foreach($raceResult in $raceResults){Api 'GET' "/auth/invites/$($raceResult.Code)"})
Check (@($raceLookups|Where-Object {$_.Status -eq 200}).Count -eq 1 -and @($raceLookups|Where-Object {$_.Status -eq 404}).Count -eq 1) 'Only one concurrent rotation code remains valid'
$raceResident=Api 'GET' '/me' $null $residentToken
Check ($raceResident.Status -eq 200 -and $raceResident.Body.role -eq 'RESIDENT') 'Concurrent rotation still preserves approved resident access'
Write-Host "Completed $script:Count invite-control HTTP checks. Synthetic tenants INVITE QA A/B $stamp remain; no records were deleted."
