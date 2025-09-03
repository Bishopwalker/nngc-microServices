# Docker Development Workflow

## Quick Start

```bash
# Start development environment
dev-start.bat

# Check status
docker-compose -f docker-compose.dev.yml ps

# Stop everything
dev-stop.bat
```

## Development Commands

### Make Code Changes & Rebuild
```bash
# Rebuild specific service after code changes
dev-rebuild.bat registration-service

# Rebuild all services
dev-rebuild.bat all
```

### View Logs
```bash
# Follow logs for a service
dev-logs.bat registration-service

# View last 100 lines
dev-logs.bat registration-service 100
```

### Individual Service Management
```bash
# Restart just one service
docker-compose -f docker-compose.dev.yml restart registration-service

# Stop one service
docker-compose -f docker-compose.dev.yml stop registration-service

# Start one service
docker-compose -f docker-compose.dev.yml start registration-service
```

## Development Workflow

### 1. Make Code Changes
- Edit your Java files in your IDE as normal
- No need to restart anything yet

### 2. Quick Rebuild (30 seconds)
```bash
# Only rebuild the service you changed
dev-rebuild.bat registration-service
```
This will:
- Compile your Java code
- Rebuild only that Docker container
- Restart the container with new code

### 3. View Results
```bash
# Check logs to see if it started OK
dev-logs.bat registration-service

# Test your API
curl http://localhost:8085/actuator/health
```

## Hot Reload (Experimental)

For even faster development, Spring Boot DevTools is configured for hot reload:
- Make small changes to existing methods
- The service may automatically reload without rebuilding
- For new classes/major changes, use `dev-rebuild.bat`

## Service URLs

| Service | URL | Container |
|---------|-----|-----------|
| Service Registry | http://localhost:8761 | nngc-service-registry-dev |
| API Gateway | http://localhost:8088 | nngc-api-gateway-dev |
| Customer Service | http://localhost:8081 | nngc-customer-service-dev |
| Registration Service | http://localhost:8085 | nngc-registration-service-dev |
| Token Service | http://localhost:8083 | nngc-token-service-dev |
| Email Service | http://localhost:8084 | nngc-email-service-dev |
| Stripe Service | http://localhost:8086 | nngc-stripe-service-dev |
| Google Service | http://localhost:8087 | nngc-google-service-dev |
| Keycloak | http://localhost:8080 | keycloak-nngc-dev |

## Debugging

### Remote Debug Setup
Add to your service's environment in docker-compose.dev.yml:
```yaml
environment:
  - JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
ports:
  - "5005:5005"  # Debug port
```

### Health Checks
```bash
# Check all services
docker-compose -f docker-compose.dev.yml ps

# Check specific service health
curl http://localhost:8085/actuator/health
```

### Database Access
```bash
# Connect to Keycloak database
docker exec -it keycloak-postgres-dev psql -U keycloak -d keycloak
```

## Tips

1. **Keep containers running** - Only rebuild when you make changes
2. **Use specific rebuilds** - `dev-rebuild.bat registration-service` is much faster than rebuilding all
3. **Monitor logs** - `dev-logs.bat service-name` while developing
4. **IDE integration** - Your IDE can still do syntax checking, code completion, etc.
5. **Git workflow** - Works normally, just rebuild after pulling changes

## Troubleshooting

### Service won't start
```bash
# Check logs for errors
dev-logs.bat service-name

# Check if port is in use
netstat -ano | findstr :8085
```

### Clean restart
```bash
# Stop everything and clean up
docker-compose -f docker-compose.dev.yml down -v

# Start fresh
dev-start.bat
```

### Database issues
```bash
# Reset Keycloak database
docker-compose -f docker-compose.dev.yml down -v keycloak-postgres
dev-start.bat
```