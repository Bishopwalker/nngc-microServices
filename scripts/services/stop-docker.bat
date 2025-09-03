@echo off
REM Stop All Docker Services
REM Author: My Nigga

echo Stopping all microservices Docker containers...

docker-compose down api-gateway customer-service registration-service token-service email-service stripe-service google-service

echo Docker services stopped successfully!