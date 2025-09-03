@echo off
REM Stop All Local Services
REM Author: My Nigga

echo Stopping all locally running microservices...

REM Kill all Java processes running Maven Spring Boot
echo Killing Maven Spring Boot processes...
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO TABLE /NH ^| findstr "spring-boot:run"') do (
    if not "%%i"=="" (
        echo Stopping process %%i
        taskkill /PID %%i /F > nul 2>&1
    )
)

REM Kill by command line pattern
wmic process where "commandline like '%%spring-boot:run%%'" delete > nul 2>&1

REM Close any CMD windows with service names in title
taskkill /FI "WINDOWTITLE eq API Gateway*" /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Customer Service*" /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Registration Service*" /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Token Service*" /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Email Service*" /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Stripe Service*" /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Google Service*" /F > nul 2>&1

echo Local services stopped successfully!