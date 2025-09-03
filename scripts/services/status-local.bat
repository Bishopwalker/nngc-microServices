@echo off
REM Check Local Services Status
REM Author: My Nigga

echo.
echo === LOCAL SERVICES STATUS ===
echo.

echo Checking for running Java processes (Spring Boot)...
tasklist /FI "IMAGENAME eq java.exe" /FO TABLE | findstr /C:"java.exe"
if errorlevel 1 echo No Java processes found.

echo.
echo Checking service endpoints...

set SERVICES=8088:API-Gateway 8081:Customer-Service 8085:Registration-Service 8083:Token-Service 8084:Email-Service 8086:Stripe-Service 8087:Google-Service

for %%s in (%SERVICES%) do (
    for /f "tokens=1,2 delims=:" %%a in ("%%s") do (
        echo Checking %%b on port %%a...
        curl -s -o nul -w "%%{http_code}" http://localhost:%%a/actuator/health 2>nul | findstr "200" > nul
        if not errorlevel 1 (
            echo   ✓ %%b is running on port %%a
        ) else (
            echo   ✗ %%b is not responding on port %%a
        )
    )
)

echo.
echo Service Registry Status:
curl -s http://localhost:8761/eureka/apps 2>nul | findstr -c "<name>" | wc -l
if not errorlevel 1 echo Registered services found in Eureka.