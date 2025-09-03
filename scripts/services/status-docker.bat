@echo off
REM Check Docker Services Status
REM Author: My Nigga

echo.
echo === DOCKER SERVICES STATUS ===
echo.

docker-compose ps api-gateway customer-service registration-service token-service email-service stripe-service google-service

echo.
echo Service Registry Status:
docker-compose ps service-registry