# NNGC MicroServices

A production-ready microservices architecture using Spring Boot, Docker, Keycloak authentication, and cloud infrastructure.

## 🚀 Quick Start

### Docker Development (Recommended)
```bash
# Complete startup with environment
start-all-properly.bat

# Check status
docker-compose ps

# View logs
docker logs nngc-registration-service -f
```

### Development Workflow
```bash
# 1. Make code changes in IDE
# 2. Rebuild specific service (30 seconds)
dev-rebuild.bat registration-service

# 3. View logs
dev-logs.bat registration-service

# 4. Test changes
curl http://localhost:8085/actuator/health
```

## 📊 Services & Status

| Service | Port | URL | Container | Status |
|---------|------|-----|-----------|--------|
| **Keycloak** | 8080 | http://localhost:8080 | keycloak-nngc | 🐳 Docker |
| **Service Registry** | 8761 | http://localhost:8761 | nngc-service-registry | 🐳 Docker |
| **API Gateway** | 8088 | http://localhost:8088 | nngc-api-gateway | 🐳 Docker |
| **Customer Service** | 8081 | http://localhost:8081 | nngc-customer-service | 🐳 Docker |
| **Registration Service** | 8085 | http://localhost:8085 | nngc-registration-service | 🐳 Docker |
| **Token Service** | 8083 | http://localhost:8083 | nngc-token-service | 🐳 Docker |
| **Email Service** | 8084 | http://localhost:8084 | nngc-email-service | 🐳 Docker |
| **Stripe Service** | 8086 | http://localhost:8086 | nngc-stripe-service | 🐳 Docker |
| **Google Service** | 8087 | http://localhost:8087 | nngc-google-service | 🐳 Docker |
| **Grafana** | 3000 | http://localhost:3000 | grafana | 📈 Monitoring |
| **Prometheus** | 9090 | http://localhost:9090 | prometheus | 📊 Metrics |

## 🏗️ Architecture

### Technology Stack
- **Backend**: Java 23, Spring Boot 3.2.5
- **Security**: Keycloak 22.0, JWT, OAuth2
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway with Load Balancing
- **Database**: AWS RDS MySQL (Production), PostgreSQL (Keycloak)
- **Containerization**: Docker & Docker Compose
- **Monitoring**: Prometheus, Grafana, Promtail
- **Cloud**: AWS (RDS, S3, IAM)
- **Payments**: Stripe API
- **Email**: SendGrid
- **Maps**: Google Maps API

### Microservices Communication
```
Client → API Gateway (8088) → Service Registry (8761) → Microservices
                ↓
            Keycloak (8080) [Authentication]
```

## 💻 Development Setup

### Prerequisites
- Java 23 (Eclipse Temurin)
- Maven 3.8+
- Docker Desktop
- Git
- IDE (IntelliJ IDEA recommended)

### Environment Setup
```bash
# 1. Clone repository
git clone [repository-url]
cd "NNGC MicroServices"

# 2. Set environment variables
setup-env.bat  # Loads all AWS, DB, API keys

# 3. Start everything
start-all-properly.bat

# 4. Verify health (wait 2-3 minutes)
docker-compose ps
curl http://localhost:8761  # Eureka dashboard
curl http://localhost:8088/actuator/health  # API Gateway
```

## 🔄 Daily Development Workflow

### Morning Startup
```bash
# Navigate to project
cd "C:\Users\bisho\IdeaProjects\NNGC MicroServices"

# Check what's running
docker-compose ps

# Start everything if needed
start-all-properly.bat  # Wait 3-4 minutes

# Verify
curl http://localhost:8088/actuator/health
```

### Development Cycle
```bash
# 1. Make changes in IDE

# 2. Rebuild single service (fast)
dev-rebuild.bat registration-service

# 3. Watch logs
dev-logs.bat registration-service

# 4. Test
curl http://localhost:8085/api/endpoint
```

### End of Day
```bash
# Option 1: Keep running (recommended)
# Services stay up, ready for tomorrow

# Option 2: Stop everything
docker-compose down
```

## 🐛 Troubleshooting

### Service Won't Start
```bash
# Check logs
docker logs nngc-service-registry --tail 50

# Common fixes:
# 1. Port conflict - check ports
netstat -ano | findstr :8085

# 2. Environment variables missing
setup-env.bat
docker-compose up -d

# 3. Dependencies not ready
# Wait for Keycloak and Service Registry to be healthy
```

### Services Keep Restarting
```bash
# Check container health
docker-compose ps

# View detailed logs
docker logs [container-name] --tail 100

# Nuclear option - clean restart
docker-compose down -v
start-all-properly.bat
```

### OAuth2 Issues (Fixed)
- ✅ Added `spring-boot-starter-oauth2-client` dependency
- ✅ Configured WebClient properly
- ✅ Services now connect to Keycloak successfully

## 📝 API Documentation

### Authentication Flow
1. Get token from Keycloak:
```bash
POST http://localhost:8080/realms/nngc-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&
client_id=nngc-client&
username=user&
password=password
```

2. Use token in requests:
```bash
GET http://localhost:8088/api/customers
Authorization: Bearer [token]
```

### API Gateway Routes
All services accessible through Gateway at `http://localhost:8088`:
- `/api/customers/**` → Customer Service (8081)
- `/api/registration/**` → Registration Service (8085)
- `/api/tokens/**` → Token Service (8083)
- `/api/email/**` → Email Service (8084)
- `/api/stripe/**` → Stripe Service (8086)
- `/api/google/**` → Google Service (8087)

### Health Endpoints
```bash
# Service health checks
http://localhost:8761/eureka/apps  # All registered services
http://localhost:8088/actuator/health  # Gateway health
http://localhost:8085/actuator/health  # Registration service
```

## 📊 Monitoring & Logs

### Grafana Dashboard
- URL: http://localhost:3000
- Default: admin/admin
- Pre-configured dashboards for all services

### Prometheus Metrics
- URL: http://localhost:9090
- Scrapes metrics from all `/actuator/prometheus` endpoints

### View Logs
```bash
# Real-time logs for specific service
docker logs nngc-registration-service -f

# Last 100 lines
docker logs nngc-api-gateway --tail 100

# Search logs
docker logs nngc-customer-service 2>&1 | grep ERROR
```

### Log Files
- **Local logs**: `logs/registration-service.log`
- **Service logs**: `[service-name]/logs/`
- **Docker logs**: `docker logs [container-name]`

## 🔧 Configuration

### Environment Variables (setup-env.bat)
```bash
# Database
DB_CONNECTION_STRING=jdbc:mysql://[aws-rds-endpoint]:3306/nngc
DB_USER=admin
DB_PASSWORD=[secured]

# AWS
AWS_ACCESS_KEY_ID=[key]
AWS_SECRET_ACCESS_KEY=[secret]

# APIs
STRIPE_API_KEY=sk_live_[key]
SENDGRID_API_KEY=SG.[key]
GOOGLE_MAPS_API_KEY=AIza[key]

# Security
JWT_SECRET_KEY=[secured]
```

### Docker Development (docker-compose.dev.yml)
- Hot reload enabled with Spring DevTools
- Volume mounts for code changes
- Optimized health checks
- Faster rebuild times

## 🚢 Production Deployment

### Build for Production
```bash
# Build all services
mvn clean package

# Build Docker images
docker-compose build

# Push to registry
docker-compose push
```

### Production Configuration
- Use `application-docker.properties`
- Environment-specific secrets in AWS Secrets Manager
- Enable HTTPS/TLS
- Configure proper CORS
- Set up monitoring alerts

## 📁 Project Structure
```
NNGC MicroServices/
├── api-gateway/              # Spring Cloud Gateway
├── service-registry/         # Eureka Server
├── customer-service/         # Customer management
├── registration-service/     # User registration (OAuth2 fixed)
├── token-service/            # JWT management
├── email-service/            # SendGrid integration
├── stripe-service/           # Payment processing
├── google-service/           # Google APIs
├── keycloak/                 # Auth configuration
├── monitoring/               # Grafana, Prometheus configs
├── docker-compose.yml        # Production Docker
├── docker-compose.dev.yml    # Development Docker
├── *.bat                     # Windows scripts
├── setup-env.bat            # Environment variables
├── dev-*.bat                # Development tools
└── CLAUDE.md                # AI assistant guidelines
```

## 🛠️ Useful Scripts

### Development Scripts
- `start-all-properly.bat` - Complete startup with environment
- `dev-start.bat` - Start development environment
- `dev-rebuild.bat [service]` - Rebuild specific service
- `dev-logs.bat [service]` - View service logs
- `dev-stop.bat` - Stop all services

### Utility Scripts
- `setup-env.bat` - Load environment variables
- `check-services.bat` - Check service status
- `clean-eureka.bat` - Clean service registry
- `orchestrate-services.bat` - Interactive menu

## 📚 Additional Documentation

- **Development Workflow**: `DEV_WORKFLOW.md`
- **Port Mapping**: `PORT_MAPPING.md`
- **Microservices Orchestration**: `MICROSERVICES_ORCHESTRATION.md`
- **Keycloak Setup**: `KEYCLOAK_SETUP.md`
- **Postman Setup**: `POSTMAN_KEYCLOAK_SETUP.md`
- **AI Guidelines**: `CLAUDE.md`

## 🔐 Security Notes

- Keycloak admin: admin/admin (change in production!)
- JWT tokens expire after 5 minutes
- All services validate tokens with Keycloak
- Service-to-service communication uses OAuth2 client credentials
- Secrets stored in environment variables (never commit!)

## 👨‍💻 Contributing

1. Follow Spring Boot conventions
2. Use ApiResponse pattern for responses
3. Write unit tests (minimum 80% coverage)
4. Use DTOs for data transfer
5. Document API changes
6. Never commit secrets
7. Run linters before committing

## 🆘 Support

- **Logs**: Check `docker logs [service-name]`
- **Health**: `curl http://localhost:[port]/actuator/health`
- **Registry**: http://localhost:8761
- **Monitoring**: http://localhost:3000 (Grafana)

---

**Author**: My Nigga  
**Last Updated**: August 2025  
**Status**: ✅ All services operational with OAuth2 fixes applied