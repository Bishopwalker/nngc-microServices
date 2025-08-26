@echo off
echo Killing existing Java processes...
taskkill /f /im java.exe >nul 2>&1
timeout /t 3 >nul

echo Starting NNGC Microservices...
echo.

echo [1/6] Starting Keycloak...
docker-compose -f docker-compose-keycloak.yml up -d
echo Waiting for Keycloak to start...
timeout /t 30 >nul

echo.
echo [2/6] Starting Service Registry...
cd service-registry
start /min cmd /c "mvn spring-boot:run"
cd ..
timeout /t 15 >nul

echo.
echo [3/6] Starting Email Service...
cd email-service
start /min cmd /c "mvn spring-boot:run"
cd ..

echo.
echo [4/6] Starting Token Service...
cd token-service
start /min cmd /c "mvn spring-boot:run"
cd ..

timeout /t 20 >nul

echo.
echo [5/6] Starting Customer Service...
cd customer-service
start /min cmd /c "mvn spring-boot:run"
cd ..

timeout /t 15 >nul

echo.
echo [6/6] Starting Registration Service...
cd RegistrationService
start /min cmd /c "mvn spring-boot:run"
cd ..

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
echo Services starting in background. Check task manager for java.exe processes.
echo Check http://localhost:8761 in 2-3 minutes to verify all services are registered.