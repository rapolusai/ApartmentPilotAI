# Local-only referral reward acceptance test. Creates synthetic records and retains them for inspection.
# Never uses a production URL, deletes data, charges money, or stores tokens/provider keys in evidence.
[CmdletBinding()]
param(
 [string]$BaseUrl='http://127.0.0.1:18081/api/v1',
 [switch]$AllowSyntheticRecords,
 [string]$OutputDirectory=''
)
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'This test creates six synthetic apartments. Re-run with -AllowSyntheticRecords against a LOCAL development database only.'}
$uri=[uri]$BaseUrl
if($uri.Scheme -ne 'http' -or $uri.Host -notin @('localhost','127.0.0.1') -or $uri.UserInfo){throw 'Referral reward tests are restricted to a local development backend.'}
if([string]::IsNullOrWhiteSpace($env:APP_PROVIDER_KEY) -or $env:APP_PROVIDER_KEY.Length -lt 32){throw 'The local backend must be started with a temporary APP_PROVIDER_KEY of at least 32 characters.'}
if(!$OutputDirectory){$OutputDirectory=Join-Path ([IO.Path]::GetTempPath()) 'ApartmentPilotAI-test-results'}
New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$results=[System.Collections.Generic.List[object]]::new()
$stamp=([guid]::NewGuid().ToString('N')).Substring(0,8)
$script:last=''
function Check([bool]$ok,[string]$name){
 $results.Add([pscustomobject]@{name=$name;passed=$ok})
 if(!$ok){throw "FAIL: $name ($script:last)"}
 Write-Host "PASS: $name"
}
function Mobile { return '9'+(Get-Random -Minimum 100000000 -Maximum 999999999).ToString() }
function Api([string]$method,[string]$path,[object]$body=$null,[string]$token='',[bool]$provider=$false){
 $headers=@{}
 if($token){$headers.Authorization="Bearer $token"}
 if($provider){$headers['X-Provider-Key']=$env:APP_PROVIDER_KEY}
 $args=@{Uri="$BaseUrl$path";Method=$method;Headers=$headers;ContentType='application/json';TimeoutSec=30}
 if($null -ne $body){$args.Body=[Text.Encoding]::UTF8.GetBytes(($body|ConvertTo-Json -Depth 15 -Compress))}
 try{$value=Invoke-RestMethod @args;$script:last="$method $path -> 200";return [pscustomobject]@{Status=200;Body=$value}}
 catch{if(!$_.Exception.Response){throw};$code=[int]$_.Exception.Response.StatusCode;$script:last="$method $path -> HTTP $code";return [pscustomobject]@{Status=$code;Body=$null}}
}
function NewApartment([string]$label){
 $pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
 $registration=Api 'POST' '/auth/register' @{name="QA $label Admin";mobile=(Mobile);pin=$pin;apartmentName="QA REF $label $stamp";city='Hyderabad';flats=5}
 Check ($registration.Status -eq 200) "$label synthetic apartment registered"
 $admin=$registration.Body.token;$tenant=[guid]$registration.Body.account.tenantId
 $sub=Api 'GET' '/ops/subscription' $null $admin
 Check ($sub.Status -eq 200 -and $sub.Body.referralCode) "$label referral code created"
 $invite=Api 'POST' '/invites' @{requestKey=[guid]::NewGuid().ToString()} $admin
 Check ($invite.Status -eq 200) "$label resident invite created"
 $residentMobile=Mobile
 $join=Api 'POST' '/auth/join' @{invite=$invite.Body.code;flatLabel='A-101';name="QA $label Resident";mobile=$residentMobile;pin=$pin;residentType='OWNER'}
 Check ($join.Status -eq 200 -and $join.Body.status -eq 'PENDING') "$label resident is pending before approval"
 $members=Api 'GET' '/members' $null $admin
 $member=@($members.Body|Where-Object {$_.mobile -eq $residentMobile})[0]
 $approved=Api 'POST' "/members/$($member.id)/approve" @{verified=$true} $admin
 Check ($approved.Status -eq 200) "$label has one approved resident"
 return [pscustomobject]@{Tenant=$tenant;Admin=$admin;Code=[string]$sub.Body.referralCode}
}
function VerifyApartment($apartment,[string]$label){
 $installed=Api 'POST' '/ops/subscription/install' @{} $apartment.Admin
 Check ($installed.Status -eq 200 -and !$installed.Body.qualified) "$label install alone does not qualify"
 $verify=Api 'POST' "/provider/apartments/$($apartment.Tenant)/verify" @{verifiedBy='LOCAL_SYNTHETIC_QA';identityAndApartmentVerified=$true} '' $true
 Check ($verify.Status -eq 200 -and $verify.Body.verified) "$label activation independently verified by local test provider"
}
# Month one is the free trial for every apartment, so its invoice is always WAIVED. The referral reward is the first
# paid month after it; returns the trial invoice and that next invoice (null when none has been raised yet).
function Invoices($apartment){
 $sub=Api 'GET' '/ops/subscription' $null $apartment.Admin
 if($sub.Status -ne 200){return $null}
 $sorted=@($sub.Body.invoices|Sort-Object startsOn)
 return [pscustomobject]@{Trial=($sorted|Select-Object -First 1);Paid=($sorted|Select-Object -Skip 1 -First 1)}
}
$completed=$false
try{
 $status=Api 'GET' '/status'
 Check ($status.Status -eq 200 -and $status.Body.version -eq '0.2.0' -and $status.Body.localRegistration) 'increment02 local server confirmed'

 $root=NewApartment 'Reward Root'
 $childOne=NewApartment 'Reward Child One'
 $childTwo=NewApartment 'Reward Child Two'
 $noConsentRoot=NewApartment 'No Consent Root'
 $noConsentOne=NewApartment 'No Consent Child One'
 $noConsentTwo=NewApartment 'No Consent Child Two'

 $offer=Api 'POST' '/ops/subscription/offer' @{terms=$true} $root.Admin
 Check ($offer.Status -eq 200 -and $offer.Body.accepted -and !$offer.Body.qualified) 'consent is recorded but does not award before two activations'
 foreach($child in @($childOne,$childTwo)){
  $claim=Api 'POST' '/ops/referrals/claim' @{code=$root.Code} $child.Admin
  Check ($claim.Status -eq 200 -and $claim.Body.id) 'new apartment claims the referrer code'
 }
 foreach($apartment in @($root,$childOne,$childTwo)){VerifyApartment $apartment 'consenting reward group'}

 foreach($apartment in @($noConsentOne,$noConsentTwo)){
  $claim=Api 'POST' '/ops/referrals/claim' @{code=$noConsentRoot.Code} $apartment.Admin
  Check ($claim.Status -eq 200 -and $claim.Body.id) 'no-consent group referral is linked'
 }
 foreach($apartment in @($noConsentRoot,$noConsentOne,$noConsentTwo)){VerifyApartment $apartment 'no-consent group'}

 foreach($apartment in @($root,$childOne,$childTwo,$noConsentRoot,$noConsentOne,$noConsentTwo)){
  $invoices=Invoices $apartment
  Check ($invoices -and $invoices.Trial -and $invoices.Trial.status -eq 'WAIVED') 'free trial month is waived for every apartment'
 }
 foreach($apartment in @($root,$childOne,$childTwo)){
  $paid=(Invoices $apartment).Paid
  Check ($paid -and [decimal]$paid.credit -eq [decimal]$paid.amount -and $paid.status -eq 'WAIVED') 'qualified offer waives each participant first paid month'
 }
 foreach($apartment in @($noConsentRoot,$noConsentOne,$noConsentTwo)){
  $paid=(Invoices $apartment).Paid
  Check (!$paid -or ([decimal]$paid.credit -eq 0 -and $paid.status -ne 'WAIVED')) 'two activations without referrer consent award no subscription credit'
 }
 $graph=Api 'GET' '/ops/subscription' $null $root.Admin
 Check (@($graph.Body.referrals|Where-Object {$_.rewarded}).Count -eq 2) 'exactly two qualifying referrals are marked rewarded'
 $retry=Api 'POST' '/ops/subscription/offer' @{terms=$true} $root.Admin
 $graphRetry=Api 'GET' '/ops/subscription' $null $root.Admin
 Check ($retry.Status -eq 200 -and $retry.Body.accepted -and !$retry.Body.qualified -and @($graphRetry.Body.referrals|Where-Object {$_.rewarded}).Count -eq 2) 'consent retry cannot create a second reward'
 $completed=$true
 Write-Host "Completed $($results.Count) referral checks. Six synthetic apartments remain for inspection; no data was deleted or charged."
}finally{
 $evidence=Join-Path $OutputDirectory ("referrals-$stamp.json")
 @{run=$stamp;time=(Get-Date).ToString('o');scope='Local synthetic referral reward acceptance';checks=[object[]]$results;complete=$completed -and $results.Count -gt 0 -and @($results|Where-Object {!$_.passed}).Count -eq 0}|ConvertTo-Json -Depth 10|Set-Content -LiteralPath $evidence -Encoding UTF8
 Write-Host "Evidence: $evidence (no passwords, access tokens, mobile numbers or provider keys recorded)"
}
