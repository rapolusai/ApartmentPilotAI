[CmdletBinding()]
param([string]$BaseUrl='http://127.0.0.1:8081/api/v1',[switch]$AllowSyntheticRecords)
$ErrorActionPreference='Stop'
if(-not $AllowSyntheticRecords){throw 'Enable labelled local synthetic records explicitly.'}
if(([uri]$BaseUrl).Host -notin @('localhost','127.0.0.1')){throw 'Local backend only.'}
$script:checks=0
function Api([string]$method,[string]$path,$body=$null,[string]$token='') {
 $headers=@{};if($token){$headers.Authorization="Bearer $token"}
 $params=@{Method=$method;Uri="$BaseUrl$path";Headers=$headers;ContentType='application/json';TimeoutSec=20}
 if($null -ne $body){$params.Body=$body|ConvertTo-Json -Depth 8 -Compress}
 try{[pscustomobject]@{Status=200;Body=(Invoke-RestMethod @params)}}
 catch{if(-not $_.Exception.Response){throw};[pscustomobject]@{Status=[int]$_.Exception.Response.StatusCode;Body=$null}}
}
function Check([bool]$pass,[string]$name){if(-not $pass){throw "FAIL: $name"};$script:checks++;Write-Host "PASS: $name"}
function Mobile{return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999)}
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$a=Api POST '/auth/register' @{name='SUPPORT QA Admin A';mobile=(Mobile);pin=$pin;apartmentName="SUPPORT QA A $stamp";city='Hyderabad';flats=5}
$b=Api POST '/auth/register' @{name='SUPPORT QA Admin B';mobile=(Mobile);pin=$pin;apartmentName="SUPPORT QA B $stamp";city='Pune';flats=5}
Check ($a.Status -eq 200 -and $b.Status -eq 200) 'Two synthetic tenants created';$ta=$a.Body.token;$tb=$b.Body.token
$mobile=Mobile
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $ta
$join=Api POST '/auth/join' @{name='SUPPORT QA Resident';mobile=$mobile;pin=$pin;invite=$invite.Body.code;flatLabel='A-101';residentType='OWNER'}
Check ($join.Status -eq 200 -and $join.Body.status -eq 'PENDING') 'Resident join request is pending approval'
$members=Api GET '/members' $null $ta
$member=@($members.Body|Where-Object mobile -eq $mobile)
Check ($members.Status -eq 200 -and $member.Count -eq 1) 'Admin sees the pending resident request'
$approve=Api POST "/members/$($member[0].id)/approve" @{verified=$true} $ta
Check ($approve.Status -eq 200) 'Admin approves the resident'
$resident=Api POST '/auth/login' @{mobile=$mobile;pin=$pin;role='RESIDENT'}
Check ($resident.Status -eq 200) 'Approved resident signs in';$tr=$resident.Body.token
Check ((Api GET '/ops/support').Status -eq 401) 'Anonymous support access denied'
$payload=@{topic='Flat mapping';message='Synthetic request only';requestKey=[guid]::NewGuid().ToString()}
$saved=Api POST '/ops/support' $payload $tr
Check ($saved.Status -eq 200 -and $saved.Body.status -eq 'RECORDED') 'Resident private request persists'
$retry=Api POST '/ops/support' $payload $tr
Check ($retry.Body.id -eq $saved.Body.id) 'Support retry returns the same record'
$mine=Api GET '/ops/support' $null $tr
Check (@($mine.Body).Count -eq 1 -and @($mine.Body)[0].message -eq $payload.message) 'Support revisit reads persisted message'
Check (@((Api GET '/ops/support' $null $ta).Body).Count -eq 0) 'Admin cannot read another requester private support messages'
Check (@((Api GET '/ops/support' $null $tb).Body).Count -eq 0) 'Foreign tenant cannot read support messages'
$changed=@{topic=$payload.topic;message='Changed retry';requestKey=$payload.requestKey}
Check ((Api POST '/ops/support' $changed $tr).Status -eq 409) 'Changed support retry is rejected'
Check ((Api POST '/ops/support' @{topic='Flat mapping';message='';requestKey=[guid]::NewGuid().ToString()} $tr).Status -eq 400) 'Blank help message rejected'
Check ((Api POST '/ops/support' @{topic='Invalid';message='Test';requestKey=[guid]::NewGuid().ToString()} $tr).Status -eq 400) 'Unsupported help topic rejected'
Check ((Api POST '/ops/support' @{topic='Flat mapping';message=('x'*1001);requestKey=[guid]::NewGuid().ToString()} $tr).Status -eq 400) 'Oversized help message rejected'
Check ((Api POST '/ops/support' @{topic='Flat mapping';message='Test';requestKey='invalid'} $tr).Status -eq 400) 'Invalid submission key rejected'
Check ((Api POST '/ops/subscription/offer' @{terms=$true} $tr).Status -eq 403) 'Resident cannot enrol apartment offer'
Check ((Api POST '/ops/subscription/offer' @{terms=$false} $ta).Status -eq 400) 'Offer requires explicit rules acceptance'
$accepted=Api POST '/ops/subscription/offer' @{terms=$true} $ta
Check ($accepted.Status -eq 200 -and $accepted.Body.accepted -eq $true -and $accepted.Body.qualified -eq $false) 'Consent is recorded without granting a reward'
$acceptedAgain=Api POST '/ops/subscription/offer' @{terms=$true} $ta
Check ($acceptedAgain.Status -eq 200) 'Offer acceptance retry succeeds without duplication'
$subscription=Api GET '/ops/subscription' $null $ta
Check ($null -ne $subscription.Body.offerAcceptedAt -and $subscription.Body.offerTermsVersion -eq '2026-09-v1') 'Consent timestamp and terms version persist'
$foreign=Api GET '/ops/subscription' $null $tb
Check ($null -eq $foreign.Body.offerAcceptedAt) 'Offer consent is tenant scoped'
$audit=Api GET '/audit' $null $ta
$consent=@($audit.Body|Where-Object action -eq 'REFERRAL_OFFER_ACCEPTED')
Check ($consent.Count -eq 1) 'Offer retry creates only one audit event'
Check ((Api GET "/audit/$($consent[0].id)" $null $ta).Status -eq 200) 'Staff reads persistent activity detail'
Check ((Api GET "/audit/$($consent[0].id)" $null $tb).Status -eq 404) 'Foreign tenant activity detail denied'
Check ((Api GET "/audit/$($consent[0].id)" $null $tr).Status -eq 403) 'Resident activity detail denied'
Check ($subscription.Body.checkoutConfigured -eq $false) 'Unconfigured checkout remains labelled unavailable'
Check ((Api POST '/ops/settings/payment' @{upi='qa@bank'}).Status -eq 401) 'Anonymous settings update denied'
Check ((Api POST '/ops/settings/payment' @{upi='qa@bank'} $tr).Status -eq 403) 'Resident settings update denied'
Check ((Api POST '/ops/settings/unknown' @{upi='qa@bank'} $ta).Status -eq 400) 'Unknown settings section rejected'
Check ((Api POST '/ops/settings/payment' @{} $ta).Status -eq 400) 'Empty settings change rejected'
Check ((Api POST '/ops/settings/payment' @{upi='qa@bank';reminders=$false} $ta).Status -eq 400) 'Payment form cannot overwrite reminder controls'
Check ((Api POST '/ops/settings/payment' @{tenantId=$b.Body.account.tenantId} $ta).Status -eq 400) 'Supplied tenant ID cannot redirect a settings change'
$beforeSettings=(Api GET '/ops/settings' $null $ta).Body
Check ((Api POST '/ops/settings/payment' @{payee='QA Association';upi='qa@bank'} $ta).Status -eq 200) 'Focused payment form saves without other fields'
$afterPayment=(Api GET '/ops/settings' $null $ta).Body
Check ($afterPayment.upi -eq 'qa@bank' -and $afterPayment.reminderDays -eq $beforeSettings.reminderDays -and $afterPayment.bookingRules -eq $beforeSettings.bookingRules) 'Payment save preserves reminder and booking rules'
$androidSave=@{upi='qa-mobile@bank';requestKey=[guid]::NewGuid().ToString()}
Check ((Api POST '/ops/settings/payment' $androidSave $ta).Status -eq 200) 'Android-style command key is accepted without changing settings fields'
Check ((Api POST '/ops/settings/payment' $androidSave $ta).Status -eq 200) 'Settings command retry returns its original result'
$changedSave=@{upi='changed@bank';requestKey=$androidSave.requestKey}
Check ((Api POST '/ops/settings/payment' $changedSave $ta).Status -eq 409) 'Changed settings command retry is rejected'
Check ((Api POST '/ops/settings/payment' @{upi='bad@bank';requestKey='invalid'} $ta).Status -eq 400) 'Invalid settings command key rejected'
Check ((Api POST '/ops/settings/payment' @{ifsc='bad'} $ta).Status -eq 400) 'Invalid bank code rejected'
Check ((Api POST '/ops/settings/reminders' @{reminderDays='29'} $ta).Status -eq 400) 'Invalid reminder schedule rejected'
Check ((Api POST '/ops/settings/billing' @{lateEnabled=$false;lateFee=0;graceDays=0} $ta).Status -eq 200) 'Default zero late fee requires no extra configuration'
Check ((Api POST '/ops/settings/booking' @{bookingRules='QA common-space rules'} $ta).Status -eq 200) 'Booking policy saves independently'
Check ((Api POST '/ops/settings/verification' @{proofRequired=$true} $ta).Status -eq 200) 'Verification control saves independently'
$clients=@();$messages=@()
try {
 $tasks=@()
 foreach($change in @(@{section='payment';body=@{payee='QA concurrent account'}},@{section='reminders';body=@{reminderDays='4,8'}})) {
  $client=[System.Net.Http.HttpClient]::new();$client.DefaultRequestHeaders.Authorization=[System.Net.Http.Headers.AuthenticationHeaderValue]::new('Bearer',$ta);$clients+=,$client
  $message=[System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::Post,"$BaseUrl/ops/settings/$($change.section)")
  $message.Content=[System.Net.Http.StringContent]::new(($change.body|ConvertTo-Json -Compress),[Text.Encoding]::UTF8,'application/json');$messages+=,$message
  $tasks+=,$client.SendAsync($message)
 }
 foreach($task in $tasks){$response=$task.GetAwaiter().GetResult();Check ($response.IsSuccessStatusCode) 'Concurrent settings section accepted';$response.Dispose()}
} finally {foreach($message in $messages){$message.Dispose()};foreach($client in $clients){$client.Dispose()}}
$finalSettings=(Api GET '/ops/settings' $null $ta).Body
Check ($finalSettings.payee -eq 'QA concurrent account' -and $finalSettings.reminderDays -eq '4,8' -and $finalSettings.proofRequired -eq $true -and $finalSettings.bookingRules -eq 'QA common-space rules') 'Concurrent focused forms preserve both changes and previous controls'
Check (((Api GET '/ops/settings' $null $tb).Body).upi -ne 'qa@bank') 'Settings remain isolated from another apartment'
Write-Host "Completed $script:checks checks. Labelled SUPPORT QA tenants $stamp retained; no data deleted."
