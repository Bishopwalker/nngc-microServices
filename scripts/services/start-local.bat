@echo off
REM Start All Services Locally for Development
REM Author: My Nigga

echo Starting microservices locally for hot reload development...
echo.

REM Set environment variables
call setup-env.bat

REM Check if Service Registry is running
echo Checking if Service Registry is available...
curl -s http://localhost:8761/actuator/health > nul
if errorlevel 1 (
    echo ERROR: Service Registry not available at http://localhost:8761
    echo Please start infrastructure first: control.bat start infrastructure
    exit /b 1
)

echo Service Registry is running. Starting services...
echo.

REM Start API Gateway
echo Starting API Gateway (Port 8088)...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
timeout /t 3 /nobreak > nul

REM Start Customer Service  
echo Starting Customer Service (Port 8081)...
start "Customer Service" cmd /k "cd customer-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
timeout /t 3 /nobreak > nul

REM Start Registration Service
echo Starting Registration Service (Port 8085)...
start "Registration Service" cmd /k "cd registration-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
timeout /t 3 /nobreak > nul

REM Start Token Service
echo Starting Token Service (Port 8083)...
start "Token Service" cmd /k "cd token-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
timeout /t 3 /nobreak > nul

REM Start Email Service
echo Starting Email Service (Port 8084)...
start "Email Service" cmd /k "cd email-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
timeout /t 3 /nobreak > nul

REM Start Stripe Service
echo Starting Stripe Service (Port 8086)...
start "Stripe Service" cmd /k "cd stripe-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"
timeout /t 3 /nobreak > nul

REM Start Google Service
echo Starting Google Service (Port 8087)...
start "Google Service" cmd /k "cd google-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo All services are starting locally...
echo Each service will open in its own terminal window for monitoring.
echo.
echo Available endpoints:
echo - API Gateway:        http://localhost:8088
echo - Customer Service:   http://localhost:8081
echo - Registration:       http://localhost:8085
echo - Token Service:      http://localhost:8083
echo - Email Service:      http://localhost:8084
echo - Stripe Service:     http://localhost:8086
echo - Google Service:     http://localhost:8087
echo.
echo Note: Services may take 30-60 seconds to fully start and register with Eureka.