@echo off
REM Start All Services in Docker
REM Author: My Nigga

echo Starting microservices in Docker containers...

REM Check if Service Registry is running
echo Checking if Service Registry is available...
docker-compose ps service-registry | findstr "Up" > nul
if errorlevel 1 (
    echo ERROR: Service Registry not running
    echo Please start infrastructure first: control.bat start infrastructure
    exit /b 1
)

echo Service Registry is running. Starting services...

docker-compose up -d api-gateway customer-service registration-service token-service email-service stripe-service google-service

echo.
echo All services are starting in Docker containers...
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
echo Use 'docker-compose logs -f [service-name]' to view service logs.