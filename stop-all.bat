@echo off
echo ========================================
echo Stopping NNGC MicroServices
echo ========================================

echo Cleaning Eureka Registry...
curl -X DELETE http://localhost:8761/eureka/apps/CUSTOMER-SERVICE 2>nul
curl -X DELETE http://localhost:8761/eureka/apps/API-GATEWAY 2>nul
curl -X DELETE http://localhost:8761/eureka/apps/REGISTRATION-SERVICE 2>nul
curl -X DELETE http://localhost:8761/eureka/apps/TOKEN-SERVICE 2>nul
curl -X DELETE http://localhost:8761/eureka/apps/STRIPE-SERVICE 2>nul
curl -X DELETE http://localhost:8761/eureka/apps/EMAIL-SERVICE 2>nul
curl -X DELETE http://localhost:8761/eureka/apps/GOOGLE-SERVICE 2>nul

echo.
echo Stopping all Java processes...
taskkill /f /im java.exe >nul 2>&1

echo Stopping Docker containers...
docker-compose -f docker-compose-keycloak.yml down 2>nul

echo.
echo ========================================
echo All services stopped and registry cleaned!
echo ========================================
echo You can now run start-all.bat to restart them.
pause