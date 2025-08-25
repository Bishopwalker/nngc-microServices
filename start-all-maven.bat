@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Starting NNGC Microservices System
echo ========================================

REM Check if Maven is available
where mvn >nul 2>nul
if errorlevel 1 (
    echo ERROR: Maven not found in PATH
    echo Please install Maven or add it to your PATH
    pause
    exit /b 1
)

REM Set base directory
set BASE_DIR=%~dp0
cd /d "%BASE_DIR%"

echo [1/6] Starting Keycloak...
start "Keycloak" cmd /k "docker-compose -f docker-compose-keycloak.yml up && echo Keycloak started && pause"

REM Wait for Keycloak
echo Waiting 45 seconds for Keycloak to initialize...
timeout /t 45 /nobreak > nul

echo [2/6] Starting Service Registry (Eureka)...
start "Service Registry" cmd /k "cd service-registry && mvn spring-boot:run"

REM Wait for Service Registry
echo Waiting 20 seconds for Service Registry...
timeout /t 20 /nobreak > nul

echo [3/6] Starting Email Service...
start "Email Service" cmd /k "cd email-service && mvn spring-boot:run"

echo [4/6] Starting Token Service...
start "Token Service" cmd /k "cd token-service && mvn spring-boot:run"

REM Wait for core services
echo Waiting 30 seconds for core services...
timeout /t 30 /nobreak > nul

echo [5/6] Starting Customer Service...
start "Customer Service" cmd /k "cd customer-service && mvn spring-boot:run"

REM Wait for Customer Service
echo Waiting 25 seconds for Customer Service...
timeout /t 25 /nobreak > nul

echo [6/6] Starting Registration Service...
start "Registration Service" cmd /k "cd RegistrationService && mvn spring-boot:run"

echo.
echo ========================================
echo ALL SERVICES STARTING!
echo ========================================
echo.
echo Services will be available at:
echo - Keycloak Admin:     http://localhost:8080 (admin/admin)
echo - Service Registry:   http://localhost:8761
echo - Email Service:      http://localhost:8084  
echo - Token Service:      http://localhost:8083
echo - Customer Service:   http://localhost:8082
echo - Registration:       http://localhost:8085
echo.
echo WAIT 3-5 MINUTES for all services to fully start
echo Check http://localhost:8761 to verify all services are registered
echo.
echo Press any key to open service monitoring URLs...
pause >nul

REM Open monitoring URLs
start http://localhost:8761
start http://localhost:8080

echo System startup complete! Check the opened browser tabs.
pause