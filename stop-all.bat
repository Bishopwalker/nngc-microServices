@echo off
echo Stopping all NNGC services...

echo Killing all Java processes...
taskkill /f /im java.exe

echo Stopping Docker Keycloak...  
docker-compose -f docker-compose-keycloak.yml down

echo.
echo All services stopped!
echo You can now run start-all.bat to restart them.