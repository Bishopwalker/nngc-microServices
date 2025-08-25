@echo off
echo Starting NNGC Microservices...

REM Start Keycloak first
echo [1/6] Starting Keycloak...
start "Keycloak" cmd /c "docker-compose -f docker-compose-keycloak.yml up"

REM Wait for Keycloak to be ready
echo Waiting for Keycloak to start...
timeout /t 30 /nobreak > nul

REM Start Service Registry
echo [2/6] Starting Service Registry...
start "Service Registry" cmd /c "cd service-registry && mvn spring-boot:run"

REM Wait for Service Registry
timeout /t 15 /nobreak > nul

REM Start Email Service
echo [3/6] Starting Email Service...
start "Email Service" cmd /c "cd email-service && mvn spring-boot:run"

REM Start Token Service
echo [4/6] Starting Token Service...
start "Token Service" cmd /c "cd token-service && mvn spring-boot:run"

REM Wait for core services
timeout /t 20 /nobreak > nul

REM Start Customer Service
echo [5/6] Starting Customer Service...
start "Customer Service" cmd /c "cd customer-service && mvn spring-boot:run"

REM Wait for Customer Service
timeout /t 15 /nobreak > nul

REM Start Registration Service (legacy endpoint)
echo [6/6] Starting Registration Service...
start "Registration Service" cmd /c "cd RegistrationService && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting...
echo ========================================
echo Keycloak Admin:     http://localhost:8080 (admin/admin)
echo Service Registry:   http://localhost:8761
echo Email Service:      http://localhost:8084
echo Token Service:      http://localhost:8083  
echo Customer Service:   http://localhost:8082
echo Registration:       http://localhost:8085
echo ========================================
echo.
echo Wait 2-3 minutes for all services to fully start
echo Check Eureka at http://localhost:8761 to verify all services are registered
pause