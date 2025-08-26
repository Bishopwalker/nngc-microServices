@echo off
echo Setting up environment variables...
set DB_USERNAME=root
set DB_PASSWORD=rootpassword
set JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

echo Killing existing Java processes...
taskkill /f /im java.exe >nul 2>&1
timeout /t 3 >nul
echo ========================================
echo Starting All Services with Clean Registry
echo ========================================
echo.

REM Clean Eureka Registry first
echo Step 1: Cleaning Eureka Registry...
call clean-eureka.bat
timeout /t 2 >nul


echo Starting NNGC Microservices...

REM Start Keycloak first
echo [1/9] Starting Keycloak...
start "Keycloak" cmd /c "docker-compose -f docker-compose-keycloak.yml up"

REM Wait for Keycloak to be ready
echo Waiting for Keycloak to start...
timeout /t 20 /nobreak > nul

REM Start Service Registry
echo [2/9] Starting Service Registry...
start "Service Registry" cmd /c "cd service-registry && mvn spring-boot:run"

REM Wait for Service Registry
timeout /t 15 /nobreak > nul

REM Start API Gateway (needs registry but should be up before other services)
echo [3/9] Starting API Gateway...
start "API Gateway" cmd /c "cd api-gateway && mvn spring-boot:run"

REM Wait for API Gateway
timeout /t 10 /nobreak > nul

REM Start Google Service
echo [4/9] Starting Google Service...
start "Google Service" cmd /c "cd google-service && mvn spring-boot:run"

REM Wait for Google Service
timeout /t 5 /nobreak > nul

REM Start Stripe Service
echo [5/9] Starting Stripe Service...
start "Stripe Service" cmd /c "cd stripe-service && mvn spring-boot:run"

REM Wait for Stripe Service
timeout /t 5 /nobreak > nul

REM Start Registration Service
echo [6/9] Starting Registration Service...
start "Registration Service" cmd /c "cd registration-service && mvn spring-boot:run"

REM Wait for Registration Service
timeout /t 5 /nobreak > nul

REM Start Email Service
echo [7/9] Starting Email Service...
start "Email Service" cmd /c "cd email-service && mvn spring-boot:run"

REM Wait for Email Service
timeout /t 5 /nobreak > nul

REM Start Token Service
echo [8/9] Starting Token Service...
start "Token Service" cmd /c "cd token-service && mvn spring-boot:run"

REM Wait for Token Service
timeout /t 5 /nobreak > nul

REM Start Customer Service
echo [9/9] Starting Customer Service...
start "Customer Service" cmd /c "cd customer-service && mvn spring-boot:run"

REM Wait for Customer Service
timeout /t 5 /nobreak > nul

echo.
echo ========================================
echo All services are starting...
echo ========================================
echo Keycloak Admin:     http://localhost:8080 (admin/admin)
echo Service Registry:   http://localhost:8761
echo API Gateway:        http://localhost:8088
echo Google Service:     http://localhost:8087
echo Email Service:      http://localhost:8084
echo Token Service:      http://localhost:8083
echo Registration:       http://localhost:8085
echo Stripe Service:     http://localhost:8086
echo Customer Service:   http://localhost:8081
echo ========================================
echo.
echo Services starting in background. Check task manager for java.exe processes.
echo Check http://localhost:8761 in 2-3 minutes to verify all services are registered.
pause