@echo off
echo Killing existing Java processes...
taskkill /f /im java.exe >nul 2>&1
timeout /t 3 >nul

echo Starting NNGC Microservices...
echo.

echo [1/8] Starting Keycloak...
docker-compose -f docker-compose-keycloak.yml up -d
echo Waiting for Keycloak to start...
timeout /t 10 >nul

echo.
echo [2/8] Starting Service Registry...
cd service-registry
start /min cmd /c "mvn spring-boot:run"
cd ..
timeout /t 10 >nul

echo.
echo [3/8] Starting API Gateway...
cd api-gateway
start /min cmd /c "mvn spring-boot:run"
echo.
timeout /t 10 >nul

echo [4/8] Starting Email Service...
cd email-service
start /min cmd /c "mvn spring-boot:run"
cd ..

echo.
echo [5/8] Starting Token Service...
cd token-service
start /min cmd /c "mvn spring-boot:run"
cd ..

echo.
echo [6/8] Starting Customer Service...
cd customer-service
start /min cmd /c "mvn spring-boot:run"
cd ..

timeout /t 5 >nul

echo.
echo [7/6] Starting Registration Service...
cd registration-service
start /min cmd /c "mvn spring-boot:run"
cd ..

timeout /t 5 >nul

echo.
echo [8/8] Starting Stripe Service
cd stripe-service
start /min cmd /c "mvn spring-boot:run"
cd ..

echo.
echo [9/9] Starting Google Service
cd google-service
start /min cmd /c "mvn spring-boot:run"
cd ..

timeout /t 3 >nul

echo.
echo ========================================
echo All services are starting...WSL
echo ========================================
echo Keycloak Admin:     http://localhost:8080 (admin/admin)
echo Service Registry:   http://localhost:8761
echo API Gateway:        http://localhost:8088
echo Email Service:      http://localhost:8084
echo Token Service:      http://localhost:8083  
echo Customer Service:   http://localhost:8081
echo Registration:       http://localhost:8085
echo Stripe Service:     http://localhost:8086
echo Google Service:     http://localhost:8087
echo ========================================
echo.
echo Services starting in background. Check task manager for java.exe processes.
echo Check http://localhost:8761 in 2-3 minutes to verify all services are registered.