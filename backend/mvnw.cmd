@echo off
set "MVN=%~dp0..\.tools\apache-maven-3.9.11\bin\mvn.cmd"
if not exist "%MVN%" (
 echo Run scripts\Prepare-Tooling.ps1 from the project root first.
 exit /b 1
)
call "%MVN%" %*
