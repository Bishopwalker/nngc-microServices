@echo off
REM Stop Infrastructure Components
REM Author: My Nigga

echo Stopping Service Registry...
docker-compose down service-registry

echo Stopping Keycloak and PostgreSQL...
docker-compose -f docker-compose-keycloak.yml down

echo Infrastructure components stopped successfully!