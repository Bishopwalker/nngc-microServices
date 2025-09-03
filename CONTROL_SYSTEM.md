# NNGC MicroServices Control System

Author: My Nigga

## Overview
Unified control system for managing NNGC microservices infrastructure and services. No more guessing which script to run!

## Quick Start

### 1. Setup Environment
```bash
# Copy and configure your environment
copy .env.example .env
# Edit .env with your actual configuration values
```

### 2. Development Workflow (Recommended)
```bash
# Start infrastructure in Docker (Keycloak, PostgreSQL, Service Registry)
control.bat start infrastructure

# Start services locally for hot reload development
control.bat start services local

# Optional: Start monitoring
control.bat start monitoring
```

### 3. Production Workflow
```bash
# Start everything in Docker containers
control.bat start all docker
```

## Commands

### Main Control Script: `control.bat`

**Syntax**: `control.bat [command] [target] [mode]`

#### Commands
- `start` - Start the specified target
- `stop` - Stop the specified target  
- `restart` - Restart the specified target
- `status` - Show status of the specified target

#### Targets
- `all` - All components (infrastructure + services + monitoring)
- `infrastructure` - Keycloak, PostgreSQL, Service Registry
- `services` - All microservices (API Gateway, Customer, Registration, etc.)
- `monitoring` - Prometheus, Grafana, Loki stack

#### Modes
- `local` - Run services locally for development (hot reload)
- `docker` - Run services in Docker containers

## Examples

```bash
# Start everything with services running locally (development)
control.bat start all local

# Start only infrastructure (Docker)
control.bat start infrastructure

# Start services in Docker containers
control.bat start services docker

# Stop everything
control.bat stop all

# Restart services locally
control.bat restart services local

# Show status of all components
control.bat status all
```

## Service Ports

### Infrastructure
- **Keycloak**: http://localhost:8080 (admin/admin)
- **PostgreSQL**: localhost:5433 (keycloak/keycloak)
- **Service Registry**: http://localhost:8761

### Microservices
- **API Gateway**: http://localhost:8088
- **Customer Service**: http://localhost:8081
- **Registration Service**: http://localhost:8085
- **Token Service**: http://localhost:8083
- **Email Service**: http://localhost:8084
- **Stripe Service**: http://localhost:8086
- **Google Service**: http://localhost:8087

### Monitoring
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Loki**: http://localhost:3100

## Architecture

```
├── control.bat                    # Main control script
├── setup-env.bat                 # Environment setup
├── .env.example                  # Environment template
├── .env                          # Your actual configuration (create this)
├── scripts/
│   ├── infrastructure/
│   │   ├── start-infrastructure.bat
│   │   ├── stop-infrastructure.bat
│   │   └── status-infrastructure.bat
│   └── services/
│       ├── start-local.bat       # Start services locally
│       ├── stop-local.bat        # Stop local services
│       ├── status-local.bat      # Check local services
│       ├── start-docker.bat      # Start services in Docker
│       ├── stop-docker.bat       # Stop Docker services
│       └── status-docker.bat     # Check Docker services
├── docker-compose.yml            # Main services
├── docker-compose-keycloak.yml   # Keycloak + PostgreSQL
└── docker-compose-monitoring.yml # Monitoring stack
```

## Development Tips

### Hot Reload Development
1. Start infrastructure: `control.bat start infrastructure`
2. Start services locally: `control.bat start services local`
3. Each service opens in its own terminal window
4. Make code changes and they'll automatically reload

### Monitoring
- Use `control.bat status all` to check everything
- View logs: `docker-compose logs -f [service-name]`
- For local services, check the individual terminal windows

### Troubleshooting
1. Always start infrastructure before services
2. Check `.env` file is properly configured
3. Ensure Docker is running for infrastructure components
4. Use `control.bat status all` to diagnose issues

## Environment Variables

All sensitive configuration is stored in `.env` file. Never commit this file to git!

Required variables:
- Database credentials
- JWT secret key
- AWS credentials  
- Google API keys
- SendGrid API key
- Stripe API keys

See `.env.example` for complete list and format.