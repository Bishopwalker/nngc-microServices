@echo off
REM Check Infrastructure Status
REM Author: My Nigga

echo.
echo === INFRASTRUCTURE STATUS ===
echo.

echo Keycloak & PostgreSQL:
docker-compose -f docker-compose-keycloak.yml ps

echo.
echo Service Registry:
docker-compose ps service-registry

echo.
echo Docker Network:
docker network ls | findstr nngc