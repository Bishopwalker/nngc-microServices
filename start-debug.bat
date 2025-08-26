@echo on
echo Starting debug version...
echo Current directory: %CD%
echo.

echo Step 1: Killing existing Java processes...
taskkill /f /im java.exe
echo Java kill result: %ERRORLEVEL%
timeout /t 3

echo.
echo Step 2: Checking Docker...
docker --version
echo Docker check result: %ERRORLEVEL%

echo.
echo Step 3: Checking Maven...
mvn --version
echo Maven check result: %ERRORLEVEL%

echo.
echo Step 4: Checking if directories exist...
if exist "service-registry" (
    echo ✓ service-registry directory found
) else (
    echo ✗ service-registry directory NOT found
)

if exist "docker-compose-keycloak.yml" (
    echo ✓ docker-compose-keycloak.yml found
) else (
    echo ✗ docker-compose-keycloak.yml NOT found
)

echo.
echo Step 5: Testing simple start...
echo Starting Service Registry only...
start "Service Registry" cmd /c "cd service-registry && echo Started Service Registry && mvn spring-boot:run"

echo.
echo Debug script completed. Check if Service Registry window opened.
pause