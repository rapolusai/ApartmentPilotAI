[CmdletBinding()]
param([string]$BaseUrl='http://127.0.0.1:8081/api/v1',[switch]$AllowSyntheticRecords)
$ErrorActionPreference='Stop'
if(-not $AllowSyntheticRecords){throw 'Explicitly enable labelled synthetic local records.'}
if(([uri]$BaseUrl).Host -notin @('localhost','127.0.0.1')){throw 'Local backend only.'}
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
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8);$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
Check ((Api GET '/status').Body.localRegistration -eq $true) 'Local registration explicitly enabled'
$a=Api POST '/auth/register' @{name='FILE QA Admin A';mobile=(Mobile);pin=$pin;apartmentName="FILE QA A $stamp";city='Hyderabad';flats=5}
$b=Api POST '/auth/register' @{name='FILE QA Admin B';mobile=(Mobile);pin=$pin;apartmentName="FILE QA B $stamp";city='Pune';flats=5}
Check ($a.Status -eq 200 -and $b.Status -eq 200) 'Isolated synthetic apartments created';$ta=$a.Body.token;$tb=$b.Body.token
$mobile=Mobile
$invite=Api POST '/invites' @{requestKey=(Key)} $ta
$join=Api POST '/auth/join' @{name='FILE QA Resident';mobile=$mobile;pin=$pin;invite=$invite.Body.code;flatLabel='A-101';residentType='OWNER'}
Check ($join.Body.status -eq 'PENDING') 'Resident requires approval'
$member=@((Api GET '/members' $null $ta).Body|Where-Object mobile -eq $mobile)[0]
Check ((Api POST "/members/$($member.id)/approve" @{verified=$true} $ta).Status -eq 200) 'Resident approved'
$login=Api POST '/auth/login' @{mobile=$mobile;pin=$pin;role='RESIDENT'};Check ($login.Status -eq 200) 'Resident signs in';$tr=$login.Body.token
$expense=@{title='FILE QA Public draft';category='Water';amount=10;paidOn=(Get-Date -Format 'yyyy-MM-dd');paid=$false;visibleToResidents=$true;mode='CASH';notes='Synthetic file tests';requestKey=(Key)}
$created=Api POST '/ops/expenses' $expense $ta;Check ($created.Status -eq 200) 'Public unpaid expense created';$id=$created.Body.id
$path="/ops/files?kind=EXPENSE&parentId=$id"
Check ((Api GET $path $null $ta).Body.Count -eq 0) 'Staff sees truthful empty attachment list'
Check ((Api GET $path).Status -eq 401) 'Anonymous list denied'
Check ((Api GET $path $null $tb).Status -eq 404) 'Foreign tenant parent list denied'
Check ((Api GET $path $null $tr).Status -eq 403) 'Unpaid expense attachments hidden from residents'
$png='iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+j3ioAAAAASUVORK5CYII='
$upload=@{kind='EXPENSE';parentId=$id;name='../bill.png';content=$png}
Check ((Api POST '/ops/files' $upload $tr).Status -eq 403) 'Resident cannot upload expense proof'
Check ((Api POST '/ops/files' $upload $tb).Status -eq 404) 'Foreign tenant cannot attach to guessed expense'
$file=Api POST '/ops/files' $upload $ta;Check ($file.Status -eq 200 -and $file.Body.mime -eq 'image/png') 'PNG stored with server-derived content type';$fid=$file.Body.id
Check ((Api POST '/ops/files' $upload $ta).Body.id -eq $fid) 'Repeated contents reuse the same attachment'
$loaded=Api GET "/ops/files/$fid" $null $ta
Check ($loaded.Status -eq 200 -and $loaded.Body.content -eq $png) 'Staff content round-trip preserves bytes'
Check ($loaded.Body.name -notmatch '[\\/]') 'Stored filename has no path separators'
Check ((Api GET "/ops/files/$fid").Status -eq 401) 'Anonymous content denied'
Check ((Api GET "/ops/files/$fid" $null $tb).Status -eq 404) 'Foreign tenant content denied'
Check ((Api GET "/ops/files/$fid" $null $tr).Status -eq 403) 'Resident cannot guess a draft file ID'
$invalid=$upload.Clone();$invalid.content='invalid base64'
Check ((Api POST '/ops/files' $invalid $ta).Status -eq 400) 'Invalid base64 rejected'
$invalid.content=[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes('<html>unsafe</html>'));$invalid.name='disguised.png'
Check ((Api POST '/ops/files' $invalid $ta).Status -eq 400) 'Disguised unsupported content rejected'
$invalid.parentId=[guid]::NewGuid().ToString();$invalid.content=$png
Check ((Api POST '/ops/files' $invalid $ta).Status -eq 404) 'Unknown expense parent denied'
$expense.requestKey=Key;$expense.paid=$true
Check ((Api POST "/ops/expenses/$id" $expense $ta).Status -eq 200) 'Expense explicitly confirmed paid'
Check ((Api GET $path $null $tr).Body.Count -eq 1) 'Resident can list public paid proof'
Check ((Api GET "/ops/files/$fid" $null $tr).Body.content -eq $png) 'Resident can download permitted proof'
$settings=(Api GET '/ops/settings' $null $ta).Body
$off=Api POST '/ops/settings/verification' @{expensesVisible=$false;requestKey=(Key)} $ta
Check ($off.Status -eq 200) 'Transparency disabled through focused settings'
Check ((Api GET $path $null $tr).Status -eq 403) 'Visibility change blocks an existing attachment list'
Check ((Api GET "/ops/files/$fid" $null $tr).Status -eq 403) 'Visibility change blocks a previously known file'
Check ((Api GET "/ops/files/$fid" $null $ta).Status -eq 200) 'Staff audit access retained while transparency is off'
Check ((Api POST '/ops/settings/verification' @{expensesVisible=$true;requestKey=(Key)} $ta).Status -eq 200) 'Transparency restored in synthetic tenant'
$private=$expense.Clone();$private.title='FILE QA Private';$private.visibleToResidents=$false;$private.requestKey=Key
$privateId=(Api POST '/ops/expenses' $private $ta).Body.id
$privateFile=$upload.Clone();$privateFile.parentId=$privateId
$privateFid=(Api POST '/ops/files' $privateFile $ta).Body.id
Check ((Api GET "/ops/files/$privateFid" $null $tr).Status -eq 403) 'Paid private expense content remains hidden'
Check ((Api GET "/ops/files?kind=EXPENSE&parentId=$privateId" $null $tr).Status -eq 403) 'Paid private expense list remains hidden'
$limit=$expense.Clone();$limit.title='FILE QA Limit';$limit.paid=$false;$limit.requestKey=Key
$limitId=(Api POST '/ops/expenses' $limit $ta).Body.id
for($i=1;$i -le 5;$i++) {
 $entry=@{kind='EXPENSE';parentId=$limitId;name="synthetic-$i.pdf";content=[Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("%PDF-1.4 synthetic $i"))}
 Check ((Api POST '/ops/files' $entry $ta).Status -eq 200) "Attachment slot $i accepted"
}
$entry.content=[Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('%PDF-1.4 synthetic sixth'))
Check ((Api POST '/ops/files' $entry $ta).Status -eq 409) 'Sixth distinct attachment rejected'
Check ((Api GET "/ops/files?kind=EXPENSE&parentId=$limitId" $null $ta).Body.Count -eq 5) 'Rejected upload leaves five records'
Write-Host "Completed $script:checks expense attachment checks. Synthetic FILE QA A/B $stamp retained. Signature tests do not establish malware safety or renderability. No records deleted."
