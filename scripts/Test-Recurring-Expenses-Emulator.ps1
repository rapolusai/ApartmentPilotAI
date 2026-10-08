[CmdletBinding()]
param([switch]$AllowSyntheticRecords,[ValidatePattern('^emulator-[0-9]+$')][string]$EmulatorSerial='emulator-5554')
$ErrorActionPreference='Stop'
if(!$AllowSyntheticRecords){throw 'Use -AllowSyntheticRecords for retained localhost test fixtures.'}
. "$PSScriptRoot/Test-Login-Emulator.ps1" -AllowSyntheticRecords -HelpersOnly -EmulatorSerial $EmulatorSerial
function Check([bool]$condition,[string]$label){if(!$condition){throw "FAIL: $label"};$script:checks++;Write-Host "PASS: $label"}
function Fill([string]$label,[string]$value){
 Observe
 $field=@($script:screen.SelectNodes('//node')|Where-Object {$_.class -like '*EditText' -and $_.'content-desc' -eq $label})[0]
 if(!$field){throw "Visible native input not found: $label"}
 if($field.bounds -notmatch '^\[(\d+),(\d+)\]\[(\d+),(\d+)\]$'){throw 'Invalid input bounds'}
 Device @('shell','input','tap',([int](([int]$Matches[1]+[int]$Matches[3])/2)).ToString(),([int](([int]$Matches[2]+[int]$Matches[4])/2)).ToString())
 Device @('shell','input','keycombination','113','29')
 if($value){Device @('shell','input','text',$value.Replace(' ','%s'))}else{Device @('shell','input','keyevent','67')}
 Device @('shell','input','keyevent','4');Observe
}

$status=Api GET '/status'
Check ($status.localRegistration -eq $true) 'Local synthetic registration is enabled'
$stamp=[guid]::NewGuid().ToString('N').Substring(0,8)
$pin=(Get-Random -Minimum 100000 -Maximum 999999).ToString()
$mobile=Mobile
$apartment="RECURRING UI $stamp"
$admin=Api POST '/auth/register' @{name="RECURRING UI ADMIN $stamp";mobile=$mobile;pin=$pin;apartmentName=$apartment;city='Hyderabad';flats=5}
if(!$admin.token){throw 'Could not create labelled synthetic recurring-test apartment.'}
$today=(Get-Date).ToString('yyyy-MM-dd')
$templateTitle="RECURRINGEDIT$stamp"
$template=Api POST '/ops/recurring' @{title=$templateTitle;category='Security';amount=75;frequency='MONTHLY';nextOn=$today;active=$true;requestKey=[guid]::NewGuid().ToString()} $admin.token
if(!$template.id){throw 'Could not create the synthetic recurring template.'}
$before=Api GET "/reports/monthly?month=$((Get-Date).ToString('yyyy-MM'))" $null $admin.token

Device @('shell','am','start','-n',"$package/com.rapolus.apartmentpilotai.MainActivity")
Observe
if(@($script:screen.SelectNodes('//node')|Where-Object {$_.text -eq 'Choose login'}).Count -eq 0){throw 'Expected the emulator to be signed out before synthetic login.'}
Click 'Admin login';Credentials $mobile $pin;Click 'Sign in'
AssertScreen $apartment 'Synthetic Admin signs into the new local apartment'
Click 'Money';Click 'Recurring expenses';Click $templateTitle
AssertScreen 'Recurring expense' 'Native recurring-template detail opens'
Click 'Edit';AssertScreen 'Recurring setup' 'Native template edit form opens'
$updatedTitle="RECURRINGEDIT${stamp}X"
Fill 'Description' $updatedTitle
Fill 'Expected amount' '90'
Click 'Save template'
AssertScreen $updatedTitle 'Native template edit returns to updated detail'
$updated=Api GET "/ops/recurring/$($template.id)" $null $admin.token
Check ($updated.title -eq $updatedTitle -and [decimal]$updated.amount -eq 90) 'Template edit persists title and amount to PostgreSQL'

Click 'Prepare expense';Click 'Prepare'
AssertScreen 'Expense detail' 'Recurring cycle preparation opens a real expense detail'
AssertScreen 'Draft' 'Cycle preparation leaves the expense unpaid'
$expense=$null
$records=Api GET "/expenses?month=$((Get-Date).ToString('yyyy-MM'))" $null $admin.token
$expense=@($records|Where-Object {$_.title -eq $updatedTitle})[0]
if(!$expense){throw 'Prepared recurring expense was not returned by the tenant expense list.'}
Check (!$expense.paid -and [decimal]$expense.amount -eq 90) 'Prepared recurring expense is an unpaid snapshotted draft'

Click 'Edit / confirm paid'
AssertScreen 'Edit draft expense' 'Staff opens explicit draft payment confirmation form'
Click 'Payment completed';Click 'Save expense';Click 'Save'
AssertScreen 'Expense detail' 'Explicit paid confirmation returns to expense detail'
$paid=Api GET "/ops/expenses/$($expense.id)" $null $admin.token
$after=Api GET "/ops/recurring/$($template.id)" $null $admin.token
$expectedNext=([datetime]$today).AddMonths(1).ToString('yyyy-MM-dd')
Check ($paid.paid -eq $true) 'Native confirmation posts the prepared expense as paid'
Check ($after.nextOn -eq $expectedNext) 'Native payment advances the recurring cycle exactly once'
$report=Api GET "/reports/monthly?month=$((Get-Date).ToString('yyyy-MM'))" $null $admin.token
Check ([decimal]$report.spent -eq ([decimal]$before.spent+90)) 'Monthly report includes the confirmed expense once'
Write-Host "Completed $script:checks recurring Android/API acceptance checks on $EmulatorSerial. Synthetic apartment $apartment retained; no real money or physical device used."
