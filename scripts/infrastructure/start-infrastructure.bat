@echo off
REM Start Infrastructure Components
REM Author: My Nigga

echo Starting Keycloak and PostgreSQL...
docker-compose -f docker-compose-keycloak.yml up -d

echo Waiting for Keycloak to be ready...
timeout /t 30 /nobreak > nul

echo Starting Service Registry...
docker-compose up -d service-registry

echo Waiting for Service Registry to register...
timeout /t 20 /nobreak > nul

echo.
echo Infrastructure components started successfully!
echo.
echo Available services:
echo - Keycloak:        http://localhost:8080 (admin/admin)
echo - PostgreSQL:      localhost:5433 (keycloak/keycloak)
echo - Service Registry: http://localhost:8761
echo.