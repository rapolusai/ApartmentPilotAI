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
$a=Api POST '/auth/register' @{name='PROFILE QA Admin';mobile=(Mobile);pin=$pin;apartmentName="PROFILE QA $stamp";city='Pune';flats=5;address='Synthetic original address';pinCode='411001'}
Check ($a.Status -eq 200) 'Local fixture registration succeeds'
$b=Api POST '/auth/register' @{name='PROFILE QA Other';mobile=(Mobile);pin=$pin;apartmentName="PROFILE QA other $stamp";city='Chennai';flats=5;address='Other address';pinCode='600001'}
Check ($b.Status -eq 200) 'Other tenant fixture registration succeeds'
$body=@{name="PROFILE QA updated $stamp";city=' Hyderabad ';address=" Synthetic Street 2`nFloor 3 ";pincode=' 500001 ';tenantId=$b.Body.account.tenantId;role='ADMIN'}
Check ((Api POST '/ops/apartment' $body).Status -eq 401) 'Anonymous apartment writes are refused'
$saved=Api POST '/ops/apartment' $body $a.Body.token
Check ($saved.Status -eq 200 -and $saved.Body.name -eq $body.name -and $saved.Body.city -eq 'Hyderabad' -and $saved.Body.pincode -eq '500001') 'Admin saves trimmed bounded profile fields'
$read=Api GET '/ops/apartment' $null $a.Body.token
Check ($read.Body.address -eq "Synthetic Street 2`nFloor 3" -and $read.Body.name -eq $body.name) 'Updated multiline address is retrieved from persistence'
$other=Api GET '/ops/apartment' $null $b.Body.token
Check ($other.Body.name -eq "PROFILE QA other $stamp" -and $other.Body.address -eq 'Other address') 'Supplied tenantId cannot redirect update to another tenant'
foreach($key in @('name','city','address','pincode')) {
 $invalid=$body.Clone();$invalid[$key]=' '
 Check ((Api POST '/ops/apartment' $invalid $a.Body.token).Status -eq 400) "Blank $key is rejected"
}
foreach($code in @('50001','abcdef','5000001')) {
 $invalid=$body.Clone();$invalid.pincode=$code
 Check ((Api POST '/ops/apartment' $invalid $a.Body.token).Status -eq 400) 'Malformed PIN code rejected'
}
foreach($key in @('name','city','address')) {
 $invalid=$body.Clone();$invalid[$key]='x' * $(if($key -eq 'address'){301}else{81})
 Check ((Api POST '/ops/apartment' $invalid $a.Body.token).Status -eq 400) "Overlong $key is rejected"
}
$invalid=$body.Clone();$invalid.city=123
Check ((Api POST '/ops/apartment' $invalid $a.Body.token).Status -eq 400) 'Non-text city is rejected'
$read=Api GET '/ops/apartment' $null $a.Body.token
Check ($read.Body.name -eq $body.name -and $read.Body.pincode -eq '500001') 'Invalid submissions do not partially rewrite persisted fields'
$invite=Api POST '/invites' @{requestKey=[guid]::NewGuid().ToString()} $a.Body.token
$mobile=Mobile
Check ((Api POST '/auth/join' @{invite=$invite.Body.code;flatLabel='A-101';name='PROFILE QA Resident';mobile=$mobile;pin=$pin;residentType='OWNER'}).Status -eq 200) 'Pending resident fixture created'
$members=Api GET '/members' $null $a.Body.token;$member=@($members.Body|Where-Object {$_.mobile -eq $mobile})[0]
Check ((Api POST "/members/$($member.id)/approve" @{verified=$true} $a.Body.token).Status -eq 200) 'Resident fixture explicitly verified by Admin'
$resident=Api POST '/auth/login' @{mobile=$mobile;pin=$pin;role='RESIDENT'}
Check ($resident.Status -eq 200) 'Approved resident authenticates'
Check ((Api POST '/ops/apartment' $body $resident.Body.token).Status -eq 403) 'Resident cannot update apartment even with supplied Admin role'
$audit=Api GET '/audit' $null $a.Body.token
Check (@($audit.Body|Where-Object {$_.action -eq 'APARTMENT_UPDATED' -and $_.recordId -eq $a.Body.account.tenantId}).Count -eq 1) 'Successful update has exactly one tenant-scoped audit; rejected writes do not audit as success'
Write-Host "Completed $script:checks bounded apartment profile checks. PROFILE QA $stamp retained. Credentials not printed."
