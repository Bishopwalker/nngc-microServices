@echo off
REM NNGC MicroServices Control Script
REM Author: My Nigga
REM Usage: control.bat [command] [target] [mode]

setlocal enabledelayedexpansion

if "%1"=="" (
    call :showHelp
    exit /b 1
)

set COMMAND=%1
set TARGET=%2
set MODE=%3

REM Default values
if "%TARGET%"=="" set TARGET=all
if "%MODE%"=="" set MODE=local

REM Validate commands
if not "%COMMAND%"=="start" if not "%COMMAND%"=="stop" if not "%COMMAND%"=="restart" if not "%COMMAND%"=="status" (
    echo ERROR: Invalid command '%COMMAND%'
    call :showHelp
    exit /b 1
)

REM Validate targets
if not "%TARGET%"=="all" if not "%TARGET%"=="infrastructure" if not "%TARGET%"=="services" if not "%TARGET%"=="monitoring" (
    echo ERROR: Invalid target '%TARGET%'
    call :showHelp
    exit /b 1
)

REM Validate modes
if not "%MODE%"=="local" if not "%MODE%"=="docker" (
    echo ERROR: Invalid mode '%MODE%'
    call :showHelp
    exit /b 1
)

echo.
echo ================================
echo NNGC MicroServices Controller
echo ================================
echo Command: %COMMAND%
echo Target:  %TARGET%
echo Mode:    %MODE%
echo ================================
echo.

REM Execute the command
if "%COMMAND%"=="start" call :startTarget
if "%COMMAND%"=="stop" call :stopTarget
if "%COMMAND%"=="restart" call :restartTarget
if "%COMMAND%"=="status" call :statusTarget

goto :eof

:startTarget
echo Starting %TARGET% in %MODE% mode...
if "%TARGET%"=="all" (
    call :startInfrastructure
    timeout /t 30 /nobreak > nul
    call :startServices
    if "%MODE%"=="docker" call :startMonitoring
) else if "%TARGET%"=="infrastructure" (
    call :startInfrastructure
) else if "%TARGET%"=="services" (
    call :startServices
) else if "%TARGET%"=="monitoring" (
    call :startMonitoring
)
goto :eof

:stopTarget
echo Stopping %TARGET% in %MODE% mode...
if "%TARGET%"=="all" (
    call :stopServices
    call :stopMonitoring
    call :stopInfrastructure
) else if "%TARGET%"=="infrastructure" (
    call :stopInfrastructure
) else if "%TARGET%"=="services" (
    call :stopServices
) else if "%TARGET%"=="monitoring" (
    call :stopMonitoring
)
goto :eof

:restartTarget
echo Restarting %TARGET% in %MODE% mode...
call :stopTarget
timeout /t 10 /nobreak > nul
call :startTarget
goto :eof

:statusTarget
echo Checking status of %TARGET%...
if "%TARGET%"=="all" (
    call :statusInfrastructure
    call :statusServices
    call :statusMonitoring
) else if "%TARGET%"=="infrastructure" (
    call :statusInfrastructure
) else if "%TARGET%"=="services" (
    call :statusServices
) else if "%TARGET%"=="monitoring" (
    call :statusMonitoring
)
goto :eof

:startInfrastructure
echo.
echo [INFRASTRUCTURE] Starting infrastructure components...
call scripts\infrastructure\start-infrastructure.bat
echo Infrastructure started successfully!
goto :eof

:stopInfrastructure
echo.
echo [INFRASTRUCTURE] Stopping infrastructure components...
call scripts\infrastructure\stop-infrastructure.bat
echo Infrastructure stopped!
goto :eof

:statusInfrastructure
echo.
echo [INFRASTRUCTURE] Status:
call scripts\infrastructure\status-infrastructure.bat
goto :eof

:startServices
echo.
echo [SERVICES] Starting microservices in %MODE% mode...
if "%MODE%"=="docker" (
    call scripts\services\start-docker.bat
) else (
    call scripts\services\start-local.bat
)
echo Services started successfully!
goto :eof

:stopServices
echo.
echo [SERVICES] Stopping microservices in %MODE% mode...
if "%MODE%"=="docker" (
    call scripts\services\stop-docker.bat
) else (
    call scripts\services\stop-local.bat
)
echo Services stopped!
goto :eof

:statusServices
echo.
echo [SERVICES] Status:
if "%MODE%"=="docker" (
    call scripts\services\status-docker.bat
) else (
    call scripts\services\status-local.bat
)
goto :eof

:startMonitoring
echo.
echo [MONITORING] Starting monitoring stack...
docker-compose -f docker-compose-monitoring.yml up -d
echo Monitoring started successfully!
goto :eof

:stopMonitoring
echo.
echo [MONITORING] Stopping monitoring stack...
docker-compose -f docker-compose-monitoring.yml down
echo Monitoring stopped!
goto :eof

:statusMonitoring
echo.
echo [MONITORING] Status:
docker-compose -f docker-compose-monitoring.yml ps
goto :eof

:showHelp
echo.
echo NNGC MicroServices Control Script
echo Author: My Nigga
echo.
echo Usage: control.bat [command] [target] [mode]
echo.
echo COMMANDS:
echo   start    - Start the specified target
echo   stop     - Stop the specified target
echo   restart  - Restart the specified target
echo   status   - Show status of the specified target
echo.
echo TARGETS:
echo   all            - All components (infrastructure + services + monitoring)
echo   infrastructure - Keycloak, PostgreSQL, Service Registry
echo   services       - All microservices (API Gateway, Customer, Registration, etc.)
echo   monitoring     - Prometheus, Grafana, Loki stack
echo.
echo MODES:
echo   local    - Run services locally for development (hot reload)
echo   docker   - Run services in Docker containers
echo.
echo EXAMPLES:
echo   control.bat start all local          # Start everything with services running locally
echo   control.bat start infrastructure     # Start only infrastructure (Docker)
echo   control.bat start services docker    # Start services in Docker containers
echo   control.bat stop all                 # Stop everything
echo   control.bat restart services local   # Restart services locally
echo   control.bat status all               # Show status of all components
echo.
echo DEVELOPMENT WORKFLOW:
echo   1. control.bat start infrastructure  # Start Docker infrastructure
echo   2. control.bat start services local  # Start services locally for hot reload
echo   3. control.bat start monitoring      # Optional: Start monitoring
echo.
echo PRODUCTION WORKFLOW:
echo   1. control.bat start all docker      # Start everything in Docker
echo.
goto :eof